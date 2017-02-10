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
      error => this.setState({error: error}));

    ajax.getWithAuth("/api/users",
      result => {
        this.setState({
          user: User.fromJSON(result)
        });
      }, error => this.setState({error: error}));
  }

  addPodcast(event: React.FormEvent<HTMLButtonElement>) {
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
      return <p>Please wait</p>;
    }

    return (
      <div>
        <div className="flex-row">
          <h1 className="title">{capitalize(user.name)}s Podcasts</h1>
          <button className="button" onClick={this.addPodcast}><span className="icon-plus"/></button>
        </div>
        {this.state.error && <div className="error"><strong>Error:</strong> {this.state.error.message}</div>}
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
        <Link to={`/app/podcasts/${item.id}/page/0`}>
          <img className="podcast-image" src={item.getThumbnailUrl(300)}/>
        </Link>
        <figcaption>{item.title}</figcaption>
      </figure>
    </div>;
  }
}