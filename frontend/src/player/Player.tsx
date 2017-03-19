import * as React from "react";
import FeedItem from "../model/FeedItem";
import * as moment from "moment";
import {formatDuration} from "../common/util";
import {Howl} from "howler";

const DEBUG = true;

interface PlayerProps {
  callbackInterval: number;
  callbackHandler: (f: FeedItem) => void;
  item?: FeedItem;

  loadFinishedCallback?: (player: Howl | null) => void;
  getNextItem?: (lastItem?: FeedItem) => FeedItem;
}

interface PlayerState {
  state: State;
  played: number;
}

enum State {
  None, Loading, LoadFinished, Paused, Playing
}

function stateToString(state: State): string {
  switch (state) {
    case State.None:
      return "None";
    case State.Loading:
      return "Loading";
    case State.LoadFinished:
      return "LoadFinished";
    case State.Paused:
      return "Pausee";
    case State.Playing:
      return "Playing";
  }
  throw Error("missing enum!");
}

class PlayerStateMachine {
  state: State;

  constructor(state: State) {
    this.state = state
  }

  itemSelected(): PlayerStateMachine {
    let newState = this.state;

    switch (this.state) {
      case State.None:
        newState = State.Loading;
        break;
      case State.Loading:
        break;
      case State.LoadFinished:
        break;
      case State.Paused:
        break;
      case State.Playing:
        break;
    }

    return new PlayerStateMachine(newState);
  }

  onLoad(): PlayerStateMachine {
    let newState = this.state;

    switch (this.state) {
      case State.None:
        throw Error("None -> onload");
      case State.Loading:
        newState = State.LoadFinished;
        break;
      case State.LoadFinished:
        break;
      case State.Paused:
        newState = State.LoadFinished;
        break;
      case State.Playing:
        newState = State.LoadFinished;
        break;
    }
    return new PlayerStateMachine(newState);
  }

  play(): PlayerStateMachine {
    let newState = this.state;

    switch (this.state) {
      case State.None:
        throw Error("None -> play");
      case State.Loading:
        newState = State.Loading;
        break;
      case State.LoadFinished:
        newState = State.Playing;
        break;
      case State.Paused:
        newState = State.Playing;
        break;
      case State.Playing:
        newState = State.Playing;
        break;
    }

    return new PlayerStateMachine(newState);
  }

  pause(): PlayerStateMachine {
    let newState = this.state;

    switch (this.state) {
      case State.None:
        throw Error("None -> pause");
      case State.Loading:
        newState = State.Loading;
        break;
      case State.LoadFinished:
        newState = State.Paused;
        break;
      case State.Paused:
        newState = State.Paused;
        break;
      case State.Playing:
        newState = State.Paused;
        break;
    }

    return new PlayerStateMachine(newState);
  }

  seek(): PlayerStateMachine {
    let newState = this.state;

    switch (this.state) {
      case State.None:
        throw Error("None -> seek");
      case State.Loading:
        newState = State.Loading;
        break;
      case State.LoadFinished:
        newState = State.Loading;
        break;
      case State.Paused:
        newState = State.Loading;
        break;
      case State.Playing:
        newState = State.Loading;
        break;
    }

    return new PlayerStateMachine(newState);
  }
}

export default class Player extends React.Component<PlayerProps, PlayerState> {
  player: Howl | null;
  timePlayedInterval?: number;
  callbackInterval?: number;

  constructor(props: PlayerProps) {
    super(props);
    this.state = {
      state: State.None,
      played: 0
    };

    this.play = this.play.bind(this);
    this.pause = this.pause.bind(this);
    this.onCanPlay = this.onCanPlay.bind(this);
    this.onEnded = this.onEnded.bind(this);
    this.onStepBack = this.onStepBack.bind(this);
    this.onStepForward = this.onStepForward.bind(this);
    this.forceRefresh = this.forceRefresh.bind(this);
    this.onPlayPause = this.onPlayPause.bind(this);
    this.initSound = this.initSound.bind(this);
    this.resetSound = this.resetSound.bind(this);
  }

  onEnded() {
    this.setState({
      state: State.None
    });
  }

  forceRefresh() {
    if (this.props.item && this.player) {
      this.props.item.lastPosition = moment.duration(Math.round(this.player.seek() as number), "seconds");
      this.props.callbackHandler(this.props.item);
    }
  }

  resetSound() {
    this.setState({
      state: State.Loading
    });
    this.player = null;
  }

  initSound(props: PlayerProps) {
    this.resetSound();

    if (props.item) {
      this.player = new Howl({
        src: props.item.mp3Url,
        html5: true,
        volume: 1.0,
        onload: this.onCanPlay,
      });
    }
  }

  componentWillReceiveProps(newProps: PlayerProps) {
    this.initSound(newProps);
  }

  play(): State {
    if (this.player) {
      this.player.play();
    }
    this.timePlayedInterval = window.setInterval(
      () => {
        if (this.player) {
          this.setState({
            played: this.player.seek() as number
          });
        }
      },
      1000);

    this.callbackInterval = window.setInterval(() => {
      if (this.props.item && this.player) {
        this.props.item.lastPosition = moment.duration(Math.round(this.player.seek() as number), "seconds");
        this.props.callbackHandler(this.props.item);
      } else {
        throw Error("Missing item");
      }
    }, this.props.callbackInterval * 1000);

    return State.Playing;
  }

  pause(): State {
    if (this.timePlayedInterval) {
      window.clearInterval(this.timePlayedInterval);
    }
    if (this.callbackInterval) {
      window.clearInterval(this.callbackInterval);
    }
    if (this.player) {
      this.player.pause();
    }
    return State.Paused;
  }

  onPlayPause() {
    if (!this.player) {
      this.initSound(this.props);
    }
    if (this.player === null) {
      throw Error("player null");
    }
    let newState;
    switch (this.state.state) {
      case State.None:
        newState = State.Loading;
        break;
      case State.LoadFinished:
        newState = this.play();
        break;
      case State.Playing:
        newState = this.pause();
        break;
      case State.Loading:
        newState = State.Loading;
        break;
      case State.Paused:
        newState = this.play();
        break;
      default:
        throw Error("Invalid state enum: " + this.state.state);
    }
    this.setState({
      state: newState
    });
  }

  onCanPlay() {
    if (this.props.loadFinishedCallback) {
      this.props.loadFinishedCallback(this.player);
    }
    this.setState({
      state: State.LoadFinished
    });
    if (this.props.item && this.player) {
      const pos = this.props.item.lastPosition.asSeconds();
      this.player.seek(pos);
    }
  }

  seek(percent: number) {
    // FIXME callback stuff goes here
    if (this.player) {
      this.setState({
        state: State.Loading
      });
      let target = this.player.duration() * percent;
      this.player.seek(target);
    }
  }

  onStepBack() {
    if (this.player) {
      // FIXME callback stuff goes here
      this.player.seek(this.player.seek() as number - 10);
    }
  }

  onStepForward() {
    if (this.player) {
      // FIXME callback stuff goes here
      this.player.seek(this.player.seek() as number + 10);
    }
  }

  render() {
    const item = this.props.item;
    let played = moment.duration(0);
    let duration = moment.duration(0);
    if (this.player) {
      played = moment.duration(this.player.seek() as number, "seconds");
      duration = moment.duration(this.player.duration(), "seconds");
    }

    let buttonEl = <i className="fa fa-play"/>;
    if (this.state.state === State.Playing) {
      buttonEl = <i className="fa fa-pause"/>;
    }
    if (this.state.state === State.Loading) {
      buttonEl = <i className="fa fa-spinner fa-spin fa-fw"/>;
    }
    const title = item ? item.title : "";

    return (
      <div className="d-flex mt-1 flex-column flex-lg-row flex-xl-row">
        <div className="flex-row mx-auto mx-lg-0 mx-xl-0 mb-1 flex-last flex-lg-first flex-xl-first">
          <button className="btn mr-1 step-backward" onClick={this.onStepBack} title="10 Seconds backwards">
            10 <i className="fa fa-step-backward"/>
          </button>
          <button className="btn mr-1 play-button btn-primary" onClick={this.onPlayPause} title="Pause/Play">
            {buttonEl}
          </button>
          <button className="btn step-forward mr-1" onClick={this.onStepForward} title="10 Seconds forwards">
            <i className="fa fa-step-forward"/> 10
          </button>
          <button className="btn" onClick={this.forceRefresh}>
            <i className="fa fa-refresh"/>
          </button>
        </div>
        <PlayerProgress duration={duration} played={played} title={title} seekTo={this.seek.bind(this)}/>
        &nbsp;
        {DEBUG && <span>{stateToString(this.state.state)}</span>}
      </div>
    );
  }
}

interface ProgressProps {
  played: moment.Duration;
  duration: moment.Duration;
  seekTo: (p: number) => void;
  title: string;
}

class PlayerProgress extends React.Component<ProgressProps, any> {
  constructor(props: ProgressProps) {
    super(props);

    this.progressBarClick = this.progressBarClick.bind(this);
  }

  progressBarClick(event: React.MouseEvent<HTMLDivElement>) {
    const element = event.currentTarget;

    const offset = element.offsetLeft + (element.offsetParent as HTMLBodyElement).offsetLeft;
    const x = event.pageX - offset;
    const width = element.clientWidth;
    const percent = x / width;
    this.props.seekTo(percent);
  }

  render() {
    const progress = (this.props.played.asMilliseconds() / this.props.duration.asMilliseconds()) * 100.0;
    const playedText = formatDuration(this.props.played);
    const durationText = formatDuration(this.props.duration);
    const style = {
      width: `${progress}%`,
      height: "1.2rem"
    };
    return (
      <div className="progress-container ml-2" style={{flex: "1"}}>
        <div
          className="progress w-100"
          onClick={this.progressBarClick}
        >
          <div
            className="progress-bar"
            role="progressbar"
            aria-valuenow={progress}
            aria-valuemin="0"
            aria-valuemax="100"
            style={style}
          />
        </div>
        <div className="flex-row">
          <span className="progress-title">{this.props.title}</span>
          <span className="float-right">{playedText} / {durationText}</span>
        </div>
      </div>
    );
  }
}