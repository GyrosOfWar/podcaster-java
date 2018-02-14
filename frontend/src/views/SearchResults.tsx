import * as React from "react";
import fetchWithAuth from "../common/ajax";
import Error from "../model/Error";
import FeedItem, { parseDates } from "../model/FeedItem";
import { PodcastDetailsItem } from "./PodcastDetails";
import Page from "../model/Page";
import Pagination from "../common/Pagination";

interface State {
  results?: Page<FeedItem>;
  error?: Error;
}

interface Props {
  location: { query: { q: string; page: string; } };
  itemClicked: any;
}

export default class SearchResults extends React.Component<Props, State> {
  constructor(props: any) {
    super(props);
    this.state = {};

    this.doSearch = this.doSearch.bind(this);
  }

  getPage(props: Props = this.props): number {
    return parseInt(props.location.query.page, 10) || 0;
  }

  getQuery(props: Props = this.props): string {
    return encodeURIComponent(props.location.query.q);
  }

  async doSearch(query: string, page: number) {
    try {
      const results = await fetchWithAuth<Page<FeedItem>>(`/api/search?q=${query}&page=${page}`);
      results.content.forEach(e => parseDates(e));
      this.setState({ results });
    } catch (error) {
      this.setState({ error });
    }
  }

  async componentDidMount() {
    const query = this.getQuery();
    const page = this.getPage();
    await this.doSearch(query, page);
  }

  async componentWillReceiveProps(nextProps: any) {
    const query = this.getQuery(nextProps);
    const page = this.getPage(nextProps);
    await this.doSearch(query, page);
  }

  render() {
    if (!this.state.results) {
      return <p>Please wait...</p>;
    }

    const page = this.getPage();
    const query = this.getQuery();

    return (
      <div>
        <h3>Search results</h3>
        {this.state.results.content.map((item, idx) =>
          <PodcastDetailsItem
            key={idx}
            item={item}
            itemClicked={this.props.itemClicked}
          />)
        }
        <Pagination
          page={this.state.results}
          nextLink={`/app/search?q=${query}&page=${page + 1}`}
          prevLink={`/app/search?q=${query}&page=${page - 1}`}
        />
      </div>
    );
  }
}