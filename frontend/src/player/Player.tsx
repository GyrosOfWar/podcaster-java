import * as React from "react";
import FeedItem from "../model/FeedItem";
import * as moment from "moment";
import "../styles/player.css";
import "../styles/icons.css";

interface PlayerProps {
    item?: FeedItem;
    callbackInterval: number;
    callbackHandler: (f: FeedItem) => void;
}

interface PlayerState {
    state: State;
    played: number;
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
    }

    play(): State {
        this.player.play();
        this.timePlayedInterval = window.setInterval(() => {
            this.setState({
                played: this.player.currentTime
            });
        }, 1000);

        this.callbackInterval = window.setInterval(() => {
            if (this.props.item) {
                this.props.item.lastPosition = moment.duration(Math.round(this.player.currentTime), "seconds");
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

        this.player.pause();
        return State.Paused;
    }

    onPlayPause(event: React.FormEvent<HTMLButtonElement>) {
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
        const item = this.props.item;
        if (!item) {
            return <div/>;
        }
        let played = moment.duration(0);
        let duration = moment.duration(0);
        if (this.player) {
            played = moment.duration(this.player.currentTime, "seconds");
            duration = moment.duration(this.player.duration, "seconds");
        }

        let buttonEl = <span className="icon-play"/>;
        if (this.state.state === State.Playing) {
            buttonEl = <span className="icon-pause"/>;
        }
        if (this.state.state === State.Loading) {
            buttonEl = <span className="icon-spinner"/>;
        }

        return (
            <div className="player flex-row">
                <div className="player-buttons flex-row">
                    <button className="button button-outline step-backward" onClick={this.onStepBack.bind(this)}>
                        <span className="icon-fast-bw"/>
                    </button>
                    <button className="button button-outline play-button" onClick={this.onPlayPause.bind(this)}>
                        {buttonEl}
                    </button>
                    <button className="button button-outline step-forward" onClick={this.onStepForward.bind(this)}>
                        <span className="icon-fast-fw"/>
                    </button>
                </div>
                <Progress duration={duration} played={played} title={item.title} seekTo={this.seek.bind(this)}/>
                <audio src={item.mp3Url} ref={(el) => this.player = el} onCanPlay={this.onCanPlay.bind(this)}/>
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

function formatDuration(duration: moment.Duration): string {
    function pad(n: number): string {
        return n < 10 ? "0" + n : n.toString();
    }

    const hr = duration.hours();
    const min = duration.minutes();
    const secs = duration.seconds();
    return `${pad(hr)}:${pad(min)}:${pad(secs)}`;
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
            <div className="progress-container">
                <progress ref={(el) => this.progressBar = el} max="1" value={progress}
                          className="player-progress" onClick={this.progressBarClick.bind(this)}>
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