import * as React from "react";
import * as ajax from "../common/ajax";
import Error from "../model/Error";
import FeedItem from "../model/FeedItem";
import {PodcastDetailsItem} from "./PodcastDetails";

interface SearchResultsState {
  results?: FeedItem[];
  error?: Error;
}

export default class SearchResults extends React.Component<any, SearchResultsState> {
  constructor(props: any) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
    const query = decodeURIComponent(this.props.location.query.q);

    ajax.getWithAuth("/api/search?q=" + encodeURIComponent(query),
      (result) => this.setState({
        results: result.map((r: any) => FeedItem.fromJSON(r))
      }),
      (error) => this.setState({error: error}));
  }

  render() {
    return <div>
      {this.state.results ?
        this.state.results.map((r: FeedItem) =>
          <PodcastDetailsItem item={r} itemClicked={() => {}}/>) :
        <p>Please wait..</p>}
    </div>;
  }
}