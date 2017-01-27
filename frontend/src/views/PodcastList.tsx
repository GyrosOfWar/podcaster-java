import * as React from "react";
// import {Link} from "react-router";
import RssFeed from "../model/RssFeed";
import Error from "../model/Error";
import * as ajax from "../common/ajax";
import "../styles/podcast-list.css";

interface PodcastListState {
    items: Array<RssFeed>;
    error: Error |  null;
}

export default class PodcastList extends React.Component<{}, PodcastListState> {
    constructor(props: {}) {
        super(props);
        this.state = {
            items: [],
            error: null
        };
    }

    componentDidMount() {
        ajax.getWithAuth("/api/feeds",
            result => {
                this.setState({
                    items: result.map((i: any) => RssFeed.fromJSON(i))
                });
            },
            error => this.setState({error: error}));

    }

    render() {
        if (this.state.error !== null) {
            return <div><p>{this.state.error}</p></div>;
        }

        return <div className="podcast-list">
            <h1>Podcasts</h1>
            {this.state.items.map(item => <PodcastListItem feed={item}/>)}
        </div>;
    }
}

interface PodcastListItemProps {
    feed: RssFeed;
}

class PodcastListItem extends React.Component<PodcastListItemProps, null> {
    render() {
        return <div className="podcast-list-item">
            <img className="podcast-image" src={this.props.feed.getThumbnailUrl(300)}/>
            {this.props.feed.title}
        </div>;
    }
}