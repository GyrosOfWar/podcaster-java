import * as React from "react";
import Error from "../model/Error";
import * as ajax from "../common/ajax";
import FeedItem from "../model/FeedItem";
import Page from "../model/Page";
import Pagination from "../common/Pagination";

interface PodcastDetailsState {
  items?: Page<FeedItem>;
  error?: Error;
  currentPage: number;
}

interface PodcastDetailsProps {
  params: any;
  itemClicked: (item: FeedItem) => void;
}

export default class PodcastDetails extends React.Component<PodcastDetailsProps, PodcastDetailsState> {
  constructor(props: any) {
    super(props);
    this.state = {
      currentPage: props.params.page
    };

    this.refreshPodcast = this.refreshPodcast.bind(this);
    this.randomPodcast = this.randomPodcast.bind(this);
  }

  componentDidMount() {
    const id = this.props.params.id;

    ajax.getWithAuth(`/api/feeds/${id}/items?page=${this.state.currentPage}`,
      response => {
        const page = Page.fromJSON(response, FeedItem.fromJSON);
        this.setState({
          items: page
        });
        document.title = page.content[0].feed.title;
      },
      error => {
        this.setState({error: error});
      });

    const itemId = this.props.params.itemId;
    if (itemId) {
      ajax.getWithAuth(`/api/feed_items/${itemId}`,
        response => {
          const item = FeedItem.fromJSON(response);
          this.props.itemClicked(item);
        },
        error => {
          this.setState({
            error: error
          });
        });
    }
  }

  componentWillReceiveProps?(nextProps: Readonly<PodcastDetailsProps>, nextContext: any): void {
    const id = nextProps.params.id;

    ajax.getWithAuth(`/api/feeds/${id}/items?page=${nextProps.params.page}`,
      response => {
        const page = Page.fromJSON(response, FeedItem.fromJSON);
        this.setState({
          items: page
        });
        document.title = page.content[0].feed.title;
      },
      error => {
        this.setState({error: error});
      });
  }

  refreshPodcast() {
    ajax.postWithAuth(`/api/feeds/${this.props.params.id}`,
      undefined,
      result => {
        const feeds = result.map(FeedItem.fromJSON);
        this.setState({
          items: this.state.items && this.state.items.withNewContent(feeds)
        });
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

  render() {
    if (this.state.error) {
      return <div>{this.state.error.message}</div>;
    }

    if (!this.state.items) {
      return <div>...</div>;
    }
    const id = this.props.params.id;
    const page = parseInt(this.props.params.page, 10);

    return <div>
      <div className="flex-row">
        <button className="button button-outline" onClick={this.refreshPodcast}>
          <span className="icon-spinner"/> Refresh
        </button>
        <button className="button button-outline" onClick={this.randomPodcast}>
          <span className="icon-shuffle"/> Random podcast
        </button>
      </div>
      {this.state.items.content.map(i =>
        <PodcastDetailsItem item={i} key={i.id} itemClicked={this.props.itemClicked}/>)}
      <Pagination page={this.state.items}
                  nextLink={`/app/podcasts/${id}/page/${page + 1}`}
                  prevLink={`/app/podcasts/${id}/page/${page - 1}`}/>
    </div>;
  }
}

interface PodcastDetailsItemProps {
  item: FeedItem;
  itemClicked: (i: FeedItem) => void;
}

class PodcastDetailsItem extends React.Component<PodcastDetailsItemProps, null> {
  clickItem() {
    this.props.itemClicked(this.props.item);
  }

  render() {
    const item = this.props.item;
    const description = {__html: item.description};
    return (
      <div className="podcast-details-item">
        <img className="podcast-details-image" src={item.getThumbnailUrl(120)}/>
        <div className="flex-column grow">
          <div className="podcast-title">{item.title}&nbsp;
            <small>{item.pubDate.format("DD.MM.YYYY")}</small>
          </div>
          <span>{item.getFormattedElapsedTime()}</span>
          <div className="podcast-details-description" dangerouslySetInnerHTML={description}/>
        </div>
        <div className="buttons">
          <button onClick={this.clickItem.bind(this)} className="button button-outline">
            <span className="icon-play"/>
          </button>
        </div>
      </div>
    );
  }
}