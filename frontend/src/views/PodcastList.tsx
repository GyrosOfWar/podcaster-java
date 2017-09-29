import * as React from "react";
import RssFeed from "../model/RssFeed";
import Error from "../model/Error";
import * as ajax from "../common/ajax";
import User from "../model/User";
import { capitalize } from "../common/util";
import { Link } from "react-router";
import { Alert } from "reactstrap";

interface PodcastListState {
    items: Array<RssFeed>;
    error?: Error;
    user?: User;
}

export default class PodcastList extends React.Component<{}, PodcastListState> {
    constructor(props: {}) {
        super(props);
        this.state = {
            items: []
        };

        this.addPodcast = this.addPodcast.bind(this);
    }

    componentDidMount() {
        ajax.getWithAuth("/api/feeds",
            result => {
                this.setState({
                    items: result.map((i: any) => RssFeed.fromJSON(i))
                });
            },
            error => this.setState({ error: error }));

        ajax.getWithAuth("/api/users",
            result => {
                this.setState({
                    user: User.fromJSON(result)
                });
            }, error => this.setState({ error: error }));
    }

    addPodcast() {
        // TODO replace with modal
        const url = prompt("Enter URL:");
        if (url) {
            ajax.postWithAuth(`/api/feeds?url=${encodeURIComponent(url)}`,
                undefined,
                result => {
                    const feed = RssFeed.fromJSON(result);
                    this.setState({
                        items: [...this.state.items, feed]
                    });
                },
                error => {
                    this.setState({
                        error: error
                    });
                });
        }
    }

    render() {
        const user = this.state.user;
        if (!user) {
            return <p>Please wait...</p>;
        }

        return (
            <div className="d-flex flex-column">
                <div className="d-flex flex-row">
                    <h3>{capitalize(user.name)}s Podcasts</h3>
                    <button className="btn ml-auto p-3" onClick={this.addPodcast}><i className="fa fa-plus" /></button>
                </div>
                {this.state.error && <Alert color="danger"><strong>Error:</strong> {this.state.error.message}</Alert>}
                <div className="d-flex flex-row flex-wrap">
                    {this.state.items.map(item => <PodcastListItem key={item.id} feed={item} />)}
                </div>
            </div>
        );
    }
}

interface PodcastListItemProps {
    feed: RssFeed;
}

class PodcastListItem extends React.Component<PodcastListItemProps, {}> {
    render() {
        const item = this.props.feed;
        return (
            <div className="d-flex flex-column mx-1">
                <Link to={`/app/podcasts/${item.id}/page/0`}>
                    <img src={item.getThumbnailUrl(300)} alt={item.title} />
                </Link>
                <p className="text-center figure-caption bigger">{item.title}</p>
            </div>
        );
    }
}