import * as React from "react";
import RssFeed from "../model/RssFeed";
import Error from "../model/Error";
import fetchWithAuth from "../common/ajax";
import User from "../model/User";
import * as util from "../common/util";
import { capitalize } from "../common/util";
import { Link } from "react-router";
import { Alert } from "reactstrap";

interface PodcastListItemProps {
  feed: RssFeed;
}

class PodcastListItem extends React.Component<PodcastListItemProps, {}> {
  render() {
    const item = this.props.feed;
    return (
      <div className="d-flex flex-column mx-1">
        <Link to={`/app/podcasts/${item.id}`}>
          <img src={util.getThumbnailUrl(item.id, "feeds", 300)} alt={item.title} />
        </Link>
        <p className="text-center figure-caption bigger">{item.title}</p>
      </div>
    );
  }
}

interface PodcastListState {
  items?: Array<RssFeed>;
  error?: Error;
  user?: User;
}

export default class PodcastList extends React.Component<{}, PodcastListState> {
  constructor(props: {}) {
    super(props);
    this.state = {};

    this.addPodcast = this.addPodcast.bind(this);
  }

  async componentDidMount() {
    try {
      const [items, user] = await Promise.all([
        fetchWithAuth<Array<RssFeed>>("/api/feeds"),
        fetchWithAuth<User>("/api/users")
      ]);
      const error = (items as any).message || (user as any).message;
      if (error) {
        this.setState({ error });
      } else {
        this.setState({ items, user });
      }
    } catch (error) {
      this.setState({ error });
    }
  }

  async addPodcast() {
    // TODO replace with modal
    const p = prompt("Enter URL:");
    if (p) {
      const url = p.trim();
      try {
        const newItem = await fetchWithAuth<RssFeed>(`/api/feeds?url=${encodeURIComponent(url)}`, {
          method: "POST"
        });
        this.setState({
          items: [... this.state.items || [], newItem]
        });
      } catch (error) {
        this.setState({ error });
      }
    }
  }

  render() {
    const user = this.state.user;
    const items = this.state.items;
    if (!user || !items) {
      if (this.state.error) {
        return <Alert color="danger"><strong>Error:</strong> {this.state.error.message}</Alert>;
      } else {
        return <p>Please wait...</p>;
      }
    }

    return (
      <div className="d-flex flex-column">
        <div className="d-flex flex-row">
          <h3>{capitalize(user.name)}s Podcasts</h3>
          <button className="btn ml-auto p-3" onClick={this.addPodcast}><i className="fa fa-plus" /></button>
        </div>
        {this.state.error && <Alert color="danger"><strong>Error:</strong> {this.state.error.message}</Alert>}
        <div className="d-flex flex-row flex-wrap">
          {this.state.items!!.map(item => <PodcastListItem key={item.id} feed={item} />)}
        </div>
      </div>
    );
  }
}
