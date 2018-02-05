import * as React from "react";
import Error from "../model/Error";
import * as ajax from "../common/ajax";
import FeedItem, { parseDates } from "../model/FeedItem";
import Page from "../model/Page";
import Pagination from "../common/Pagination";
import { UncontrolledAlert } from "reactstrap";
import * as util from "../common/util";
import * as moment from "moment";

interface PodcastDetailsItemProps {
  item: FeedItem;
  itemClicked: (i: FeedItem) => void;
}

export class PodcastDetailsItem extends React.Component<PodcastDetailsItemProps, {}> {
  constructor(props: PodcastDetailsItemProps) {
    super(props);
    this.clickItem = this.clickItem.bind(this);
  }

  clickItem() {
    this.props.itemClicked(this.props.item);
  }

  render() {
    const item = this.props.item;
    const description = { __html: item.description };
    const pubDate = (item.pubDate as moment.Moment).format("YYYY-MM-DD");
    return (
      <div className="d-flex my-2">
        <img
          className="pr-2 hidden-md-down"
          src={util.getThumbnailUrl(item.hashedImageUrl, 120)}
          style={{ maxHeight: "120px" }}
        />
        <div className="d-flex flex-column">
          <div className="podcast-title">
            <strong className="text-primary">{item.title}</strong>&nbsp;
            <small>{pubDate}</small>
          </div>
          <span>{util.getFormattedElapsedTime(item.lastPosition, item.duration)}</span>
          <div className="podcast-details-description" dangerouslySetInnerHTML={description} />
        </div>
        <div className="ml-auto">
          <button onClick={this.clickItem} className="btn">
            <i className="fa fa-play" />
          </button>
        </div>
      </div>
    );
  }
}

interface PodcastDetailsState {
  items?: Page<FeedItem>;
  error?: Error;
  currentPage: number;
  doingRefresh: boolean;
  info?: string;
}

interface PodcastDetailsProps {
  history: any;
  params: any;
  itemClicked: (item: FeedItem) => void;
}

export default class PodcastDetails extends React.Component<PodcastDetailsProps, PodcastDetailsState> {
  constructor(props: any) {
    super(props);
    this.state = {
      currentPage: props.params.page,
      doingRefresh: false
    };

    this.refreshPodcast = this.refreshPodcast.bind(this);
    this.randomPodcast = this.randomPodcast.bind(this);
    this.deletePodcast = this.deletePodcast.bind(this);
  }

  componentDidMount() {
    const id = this.props.params.id;

    ajax.getWithAuth(`/api/feeds/${id}/items?page=${this.state.currentPage}`,
      response => {
        const page = response as Page<FeedItem>;
        page.content.forEach(item => parseDates(item));
        this.setState({
          items: page
        });
        document.title = page.content[0].feed.title;
      },
      error => {
        this.setState({ error: error });
      });

    const itemId = this.props.params.itemId;
    if (itemId) {
      ajax.getWithAuth(`/api/feed_items/${itemId}`,
        item => {
          this.props.itemClicked(item);
        },
        error => {
          this.setState({
            error: error
          });
        });
    }
  }

  componentWillReceiveProps?(nextProps: Readonly<PodcastDetailsProps>): void {
    if (nextProps.params === this.props.params) {
      return;
    }

    const id = nextProps.params.id;

    ajax.getWithAuth(`/api/feeds/${id}/items?page=${nextProps.params.page}`,
      response => {
        const page = response as Page<FeedItem>;
        page.content.forEach(i => parseDates(i));
        this.setState({
          items: page
        });
        document.title = response.content[0].feed.title;
      },
      error => {
        this.setState({ error: error });
      });
  }

  refreshPodcast() {
    this.setState({
      doingRefresh: true
    });
    ajax.postWithAuth(`/api/feeds/${this.props.params.id}`,
      undefined,
      feeds => {
        if (feeds.length === 0) {
          this.setState({
            info: "Feed has no new items."
          });
        }

        const oldItems = this.state.items;
        if (oldItems) {
          const newContent = [...feeds, ...oldItems.content].slice(oldItems.size);
          const newPage = Object.assign({}, oldItems, { content: newContent });
          this.setState({
            doingRefresh: false,
            items: newPage
          });
        } else {
          this.setState({
            doingRefresh: false
          });
        }
      },
      error => {
        this.setState({
          error: error
        });
      });
  }

  randomPodcast() {
    const id = this.props.params.id;
    ajax.getWithAuth(`/api/feeds/${id}/random`,
      response => {
        this.props.itemClicked(response);
      },
      error => {
        this.setState({
          error: error
        });
      });
  }

  deletePodcast() {
    if (confirm("Are you sure?")) {
      const items = this.state.items;
      if (items) {
        const id = items.content[0].feed.id;
        ajax.deleteWithAuth(`/api/feeds/${id}`, () => {
          this.props.history.push("/app");
        });
      }
    }
  }

  render() {
    if (this.state.error) {
      return <div>{this.state.error.message}</div>;
    }

    if (!this.state.items) {
      return <div>...</div>;
    }
    const id = this.props.params.id;
    const page = parseInt(this.props.params.page, 10);
    let refreshClasses = "fa fa-spinner";
    if (this.state.doingRefresh) {
      refreshClasses += " fa-spin fa-fw";
    }

    return (
      <div className="d-flex flex-column">
        {this.state.info && <UncontrolledAlert color="info">{this.state.info}</UncontrolledAlert>}
        <div className="flex-row mt-2">
          <button className="btn btn-sm mr-1" onClick={this.refreshPodcast}>
            <i className={refreshClasses} /> Refresh
          </button>
          <button className="btn btn-sm mr-1" onClick={this.randomPodcast}>
            <i className="fa fa-random" /> Random podcast
          </button>
          <button className="btn btn-sm btn-danger float-right" onClick={this.deletePodcast}>
            <i className="fa fa-trash" /> Delete
          </button>
        </div>
        {this.state.items.content.map(i =>
          <PodcastDetailsItem item={i} key={i.id} itemClicked={this.props.itemClicked} />)}
        <Pagination
          page={this.state.items}
          nextLink={`/app/podcasts/${id}/page/${page + 1}`}
          prevLink={`/app/podcasts/${id}/page/${page - 1}`}
        />
      </div>
    );
  }
}
