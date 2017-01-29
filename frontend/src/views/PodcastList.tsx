import * as React from "react";
import RssFeed from "../model/RssFeed";
import Error from "../model/Error";
import * as ajax from "../common/ajax";
import "../styles/podcast-list.css";
import User from "../model/User";
import {capitalize} from "../common/util";
import {Link} from "react-router";

interface PodcastListState {
    items: Array<RssFeed>;
    error: Error |  null;
    user: User | null;
}

export default class PodcastList extends React.Component<{}, PodcastListState> {
    constructor(props: {}) {
        super(props);
        this.state = {
            items: [],
            error: null,
            user: null
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

        ajax.getWithAuth("/api/users",
            result => {
                this.setState({
                    user: User.fromJSON(result)
                });
            }, error => this.setState({error: error}));
    }

    render() {
        if (this.state.error !== null) {
            return <div><p>{this.state.error.status}: {this.state.error.message}</p></div>;
        }

        const user = this.state.user;
        if (user === null) {
            return <p>Please wait</p>;
        }

        return (
            <div>
                <div className="flex-row">
                    <h1 className="title">{capitalize(user.name)}s Podcasts</h1>
                    <button className="button"><span className="icon-plus"/></button>
                </div>
                <div className="podcast-list">
                    {this.state.items.map(item => <PodcastListItem key={item.id} feed={item}/>)}
                </div>
            </div>
        );
    }
}

interface PodcastListItemProps {
    feed: RssFeed;
}

class PodcastListItem extends React.Component<PodcastListItemProps, null> {
    render() {
        const item = this.props.feed;
        return <div className="podcast-list-item">
            <figure>
                <Link to={`/podcasts/${item.id}/page/0`}>
                    <img className="podcast-image" src={item.getThumbnailUrl(300)}/>
                </Link>
                <figcaption>{item.title}</figcaption>
            </figure>
        </div>;
    }
}