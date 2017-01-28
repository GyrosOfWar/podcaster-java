import * as React from "react";
import FeedItem from "../model/FeedItem";
import * as moment from "moment";

interface PlayerProps {
    item: FeedItem;
}

interface PlayerState {
    state: State,
}

enum State {
    None, Loading, Paused, Playing
}

export default class Player extends React.Component<PlayerProps, PlayerState> {
    player: HTMLAudioElement;

    constructor(props: PlayerProps) {
        super(props);
        this.state = {
            state: State.None
        };
    }

    render() {
        const item = this.props.item;
        const played = moment.duration(this.player.currentTime, "seconds");
        return (
            <div className="player">
                <Progress duration={item.duration} played={played}/>
                <audio src={item.mp3Url} ref={(el) => this.player = el}/>
            </div>
        );
    }
}

interface ProgressProps {
    played: moment.Duration;
    duration: moment.Duration;
}

function formatDuration(duration: moment.Duration): string {
    return moment.utc(duration.as('milliseconds')).format('mm:ss');
}

class Progress extends React.Component<ProgressProps, any> {
    render() {
        const progress = this.props.played.asMilliseconds() / this.props.duration.asMilliseconds();
        const playedText = formatDuration(this.props.played);
        const durationText = formatDuration(this.props.duration);
        return (
            <div className="progress-container">
                <progress max="1" value={progress} className="progress">{Math.floor(progress * 100)} %</progress>
                <span className="progress-text">{playedText} / {durationText}</span>
            </div>
        );
    }
}