import * as React from "react";
import fetchWithAuth from "../common/ajax";
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

  async doSearch(query: string) {
    const encoded = encodeURIComponent(query);
    try {
      const results = await fetchWithAuth<Array<FeedItem>>(`/api/search?q=${encoded}`);
      results.forEach(e => parseDates(e));
      this.setState({ results });
    } catch (error) {
      this.setState({ error });
    }
  }

  async componentDidMount() {
    const query = decodeURIComponent(this.props.location.query.q);
    await this.doSearch(query);
  }

  async componentWillReceiveProps(nextProps: any) {
    const query = decodeURIComponent(nextProps.location.query.q);
    await this.doSearch(query);
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