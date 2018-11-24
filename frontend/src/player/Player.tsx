import * as React from "react";
import FeedItem from "../model/FeedItem";
import * as moment from "moment";
import { formatDuration } from "../common/util";
import fetchWithAuth from "../common/ajax";

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
    let progress = (this.props.played.asMilliseconds() / this.props.duration.asMilliseconds()) * 100.0;
    if (!progress || isNaN(progress)) {
      progress = 0;
    }
    const playedText = formatDuration(this.props.played);
    const durationText = formatDuration(this.props.duration);
    const style = {
      width: `${progress}%`,
      height: "1.2rem"
    };
    return (
      <div className="progress-container ml-2" style={{ flex: "1" }}>
        <div
          className="progress w-100 h-50"
          onClick={this.progressBarClick}
        >
          <div
            className="progress-bar"
            role="progressbar"
            aria-valuenow={progress}
            aria-valuemin={0}
            aria-valuemax={100}
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

interface PlayerProps {
  callbackInterval: number;
  callbackHandler: (f: FeedItem) => void;
  itemChanged: (f: FeedItem) => void;
  item?: FeedItem;

  loadFinishedCallback?: (player: HTMLAudioElement) => void;
  getNextItem?: (lastItem?: FeedItem) => FeedItem;
}

interface PlayerState {
  state: State;
  played: number;
}

enum State {
  None, Loading, LoadFinished, Paused, Playing
}

export default class Player extends React.Component<PlayerProps, PlayerState> {
  player?: HTMLAudioElement;
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
    this.seek = this.seek.bind(this);
    this.favoriteItem = this.favoriteItem.bind(this);
  }

  onEnded() {
    this.setState({
      state: State.None
    });
  }

  forceRefresh() {
    if (this.props.item) {
      this.props.item.lastPosition = moment.duration(Math.round(this.player!.currentTime), "seconds");
      this.props.callbackHandler(this.props.item);
    }
  }

  play(): State {
    this.player!.play();
    this.timePlayedInterval = window.setInterval(
      () => {
        this.setState({
          played: this.player!.currentTime
        });
      },
      1000);

    this.callbackInterval = window.setInterval(() => {
      if (this.props.item) {
        this.props.item.lastPosition = moment.duration(Math.round(this.player!.currentTime), "seconds");
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

    this.player!.pause();
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
        throw Error("Invalid state enum: " + this.state.state);
    }
    this.setState({
      state: newState
    });
  }

  onCanPlay() {
    if (this.props.loadFinishedCallback) {
      this.props.loadFinishedCallback(this.player!);
    }
    this.setState({
      state: State.LoadFinished
    });
  }

  seek(percent: number) {
    this.player!.currentTime = this.player!.duration * percent;
  }

  onStepBack() {
    this.player!.currentTime -= 10;
  }

  onStepForward() {
    this.player!.currentTime += 10;
  }

  async favoriteItem() {
    if (this.props.item) {
      const isFav = !this.props.item.favorite;
      const item = Object.assign(this.props.item, { favorite: isFav });
      await fetchWithAuth(`/api/feed_items/${item.id}`, {
        method: "POST",
        body: JSON.stringify(item)
      });
      this.props.itemChanged(item);
    }
  }

  render() {
    const item = this.props.item;
    let played = moment.duration(0);
    let duration = moment.duration(0);
    if (this.player) {
      played = moment.duration(this.player.currentTime, "seconds");
      duration = moment.duration(this.player.duration, "seconds");
    }

    let buttonEl = <i className="fa fa-play" />;
    if (this.state.state === State.Playing) {
      buttonEl = <i className="fa fa-pause" />;
    }
    if (this.state.state === State.Loading) {
      buttonEl = <i className="fa fa-spinner fa-spin fa-fw" />;
    }
    const mp3Url = item ? item.mp3Url : "";
    const title = item ? item.title : "";
    let star = "fa fa-star-o";
    if (item && item.favorite) {
      star = "fa fa-star";
    }

    return (
      <div className="d-flex mt-1 flex-column flex-lg-row flex-xl-row">
        <div className="flex-row mx-auto mx-lg-0 mx-xl-0 mb-1 flex-last flex-lg-first flex-xl-first">
          <button className="btn mr-1 step-backward" onClick={this.onStepBack} title="10 Seconds backwards">
            10 <i className="fa fa-step-backward" />
          </button>
          <button className="btn mr-1 play-button btn-primary" onClick={this.onPlayPause} title="Pause/Play">
            {buttonEl}
          </button>
          <button className="btn step-forward mr-1" onClick={this.onStepForward} title="10 Seconds forwards">
            <i className="fa fa-step-forward" /> 10
          </button>
          <button className="btn mr-1" onClick={this.forceRefresh}>
            <i className="fa fa-refresh" />
          </button>
          <button className="btn" onClick={this.favoriteItem}>
            <i className={star} />
          </button>
        </div>
        <PlayerProgress duration={duration} played={played} title={title} seekTo={this.seek} />
        <audio
          id="player-audio"
          src={mp3Url}
          ref={(el) => {
            if (el) {
              this.player = el;
            }
          }}
          onCanPlay={this.onCanPlay}
          onEnded={this.onEnded}
        />
      </div>
    );
  }
}
