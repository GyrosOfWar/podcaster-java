import * as React from "react";
import fetchWithAuth from "../common/ajax";
import Page from "../model/Page";
import HistoryEntry, { parseDates } from "../model/HistoryEntry";
import Error from "../model/Error";
import DateTimeComponent from "../common/DateTimeComponent";
import { Link } from "react-router";
import { Alert } from "reactstrap";
import * as util from "../common/util";
import * as moment from "moment";

interface HistoryEntryViewProps {
  entry: HistoryEntry;
}

class HistoryEntryView extends React.Component<HistoryEntryViewProps, {}> {
  render() {
    const entry = this.props.entry;
    const date = entry.time;
    const item = entry.feedItem;

    const feedId = item.feed.id;
    const itemId = item.id;
    let { lastPosition, duration } = item;
    if (typeof lastPosition === "string" || typeof duration === "string") {
      lastPosition = moment.duration(lastPosition);
      duration = moment.duration(duration);
    }
    return (
      <div>
        <Link to={`/app/podcasts/${feedId}/item/${itemId}`}>{entry.feedItem.title}</Link>&nbsp;
        <small><DateTimeComponent date={date}/></small>
        <span className="float-right">{util.getFormattedElapsedTime(lastPosition, duration)}</span>
      </div>
    );
  }
}

interface GroupedHistoryEntry {
  date: Array<number>;
  entries: Array<HistoryEntry>;
}

interface HistoryState {
  entries?: Page<GroupedHistoryEntry>;
  error?: Error;
}

interface HistoryProps {
  // Router history
  history: any;
  // Router params
  params: any;
}

function formatDate(arr: Array<number>): string {
  return moment(arr).format("YYYY-MM-DD");
}

export default class History extends React.Component<HistoryProps, HistoryState> {
  constructor(props: HistoryProps) {
    super(props);
    this.state = {};
  }

  async componentDidMount() {
    try {
      const page = await fetchWithAuth<Page<GroupedHistoryEntry>>("/api/users/history/grouped");
      page.content.forEach(e => e.entries.forEach(f => parseDates(f)));
      this.setState({
        entries: page
      });
    } catch (error) {
      this.setState({ error });
    }
  }

  render() {
    let error = null;
    if (this.state.error) {
      error = <Alert color="danger">{this.state.error.message}</Alert>;
    }
    const entries = this.state.entries;
    if (!entries) {
      return <p>...</p>;
    }

    const views: React.ReactElement<any>[] = [];
    let i = 0;

    entries.content.forEach(entry => {
      views.push((
        <div key={"group-" + i}>
          <strong className="text-secondary">{formatDate(entry.date)}</strong>
          {entry.entries.map(e => <HistoryEntryView entry={e} key={e.id} />)}
        </div>
      ));
      i += 1;
    });

    return (
      <div>
        <h3>History</h3>
        {error}
        {views}
      </div>
    );
  }
}