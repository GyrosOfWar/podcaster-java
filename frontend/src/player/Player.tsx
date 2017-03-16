import * as React from "react";
import FeedItem from "../model/FeedItem";
import * as moment from "moment";
import {formatDuration} from "../common/util";

interface PlayerProps {
  callbackInterval: number;
  callbackHandler: (f: FeedItem) => void;

  loadFinishedCallback?: (player: HTMLAudioElement) => void;
  getNextItem?: (lastItem?: FeedItem) => FeedItem;
}

interface PlayerState {
  state: State;
  played: number;
  item?: FeedItem;
}

enum State {
  None, Loading, LoadFinished, Paused, Playing
}

export default class Player extends React.Component<PlayerProps, PlayerState> {
  player: HTMLAudioElement;
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
    this.onEnded = this.onEnded.bind(this)
  }

  onEnded() {
    if (this.props.getNextItem) {
      this.setState({
        state: State.None,
        item: this.props.getNextItem(this.state.item)
      });
    }
  }

  play(): State {
    this.player.play();
    this.timePlayedInterval = window.setInterval(() => {
      this.setState({
        played: this.player.currentTime
      });
    }, 1000);

    this.callbackInterval = window.setInterval(() => {
      if (this.state.item) {
        this.state.item.lastPosition = moment.duration(Math.round(this.player.currentTime), "seconds");
        this.props.callbackHandler(this.state.item);
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

    this.player.pause();
    return State.Paused;
  }

  onPlayPause() {
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
        throw Error("Error: " + this.state.state);
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
  }

  seek(percent: number) {
    this.player.currentTime = this.player.duration * percent;
  }

  onStepBack() {
    this.player.currentTime -= 10;
  }

  onStepForward() {
    this.player.currentTime += 10;
  }

  render() {
    const item = this.state.item;
    let played = moment.duration(0);
    let duration = moment.duration(0);
    if (this.player) {
      played = moment.duration(this.player.currentTime, "seconds");
      duration = moment.duration(this.player.duration, "seconds");
    }

    let buttonEl = <i className="fa fa-play"/>;
    if (this.state.state === State.Playing) {
      buttonEl = <i className="fa fa-pause"/>;
    }
    if (this.state.state === State.Loading) {
      buttonEl = <i className="fa fa-spinner fa-spin fa-3x fa-fw"/>;
    }
    const mp3Url = item ? item.mp3Url : "";
    const title = item ? item.title : "";

    return (
      <div className="player d-flex">
        <div className="player-buttons flex-row">
          <button className="btn mr-1 step-backward" onClick={this.onStepBack.bind(this)}>
            <i className="fa fa-fast-backward"/>
          </button>
          <button className="btn mr-1 play-button btn-primary" onClick={this.onPlayPause.bind(this)}>
            {buttonEl}
          </button>
          <button className="btn step-forward" onClick={this.onStepForward.bind(this)}>
            <i className="fa fa-fast-forward"/>
          </button>
        </div>
        <Progress duration={duration} played={played} title={title} seekTo={this.seek.bind(this)}/>
        <audio id="player-audio" src={mp3Url} ref={(el) => this.player = el}
               onCanPlay={this.onCanPlay.bind(this)} onEnded={this.onEnded}/>
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

class Progress extends React.Component<ProgressProps, any> {
  progressBar: HTMLProgressElement;

  progressBarClick(event: React.MouseEvent<HTMLProgressElement>) {
    const offset = this.progressBar.offsetLeft + (this.progressBar.offsetParent as HTMLBodyElement).offsetLeft;
    const x = event.pageX - offset;
    const width = this.progressBar.clientWidth;
    const percent = x / width;
    this.props.seekTo(percent);
  }

  render() {
    const progress = this.props.played.asMilliseconds() / this.props.duration.asMilliseconds();
    const playedText = formatDuration(this.props.played);
    const durationText = formatDuration(this.props.duration);
    return (
      <div className="progress-container ml-2" style={{flex: "1"}}>
        <progress ref={(el) => this.progressBar = el} max="1" value={progress}
                  className="progress-bar w-100" onClick={this.progressBarClick.bind(this)}>
          {Math.floor(progress * 100)} %
        </progress>
        <div className="flex-row">
          <span className="progress-title">{this.props.title}</span>
          <span className="progress-text">{playedText} / {durationText}</span>
        </div>
      </div>
    );
  }
}