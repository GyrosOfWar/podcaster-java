import * as React from "react";
import * as ajax from "../common/ajax";
import Error from "../model/Error";
import FeedItem, { parseDates } from "../model/FeedItem";
import { PodcastDetailsItem } from "./PodcastDetails";

interface SearchResultsState {
  results?: FeedItem[];
  error?: Error;
}

export default class SearchResults extends React.Component<any, SearchResultsState> {
  constructor(props: any) {
    super(props);
    this.state = {};

    this.doSearch = this.doSearch.bind(this);
  }

  doSearch(query: string) {
    ajax.getWithAuth("/api/search?q=" + encodeURIComponent(query),
      (results) => {
        const page = results as Array<FeedItem>;
        page.forEach(e => parseDates(e));
        this.setState({
          results: page
        });
      },
      (error) => this.setState({ error: error }));
  }

  componentDidMount() {
    const query = decodeURIComponent(this.props.location.query.q);
    this.doSearch(query);
  }

  componentWillReceiveProps(nextProps: any) {
    const query = decodeURIComponent(nextProps.location.query.q);
    this.doSearch(query);
  }

  render() {
    return (
      <div>
        <h3>Search results</h3>
        {this.state.results ?
          this.state.results.map((r: FeedItem) =>
            <PodcastDetailsItem key={r.id} item={r} itemClicked={item => this.props.itemClicked(item)} />) :
          <p>Please wait..</p>}
      </div>
    );
  }
}