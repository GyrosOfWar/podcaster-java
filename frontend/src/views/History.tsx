import * as React from "react";
import * as ajax from "../common/ajax";
import Page from "../model/Page";
import HistoryEntry from "../model/HistoryEntry";
import Error from "../model/Error";
//noinspection ES6UnusedImports
import DateTimeComponent, {DisplayType} from "../common/DateTimeComponent";
import {Link} from "react-router";
import {Alert} from "reactstrap";

interface HistoryState {
  entries?: Map<string, Array<HistoryEntry>>;
  error?: Error;
}

function groupBy<Type, Key>(array: Array<Type>, keyFunc: (t: Type) => Key): Map<Key, Array<Type>> {
  const map = new Map<Key, Array<Type>>();
  for (const value of array) {
    const key = keyFunc(value);
    const entry = map.get(key);
    if (entry) {
      entry.push(value);
    } else {
      map.set(key, [value]);
    }
  }

  return map;
}

export default class History extends React.Component<null, HistoryState> {
  constructor(props: null) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
    ajax.getWithAuth("/api/users/history",
      result => {
        const page = Page.fromJSON(result, HistoryEntry.fromJSON);
        const grouped = groupBy(page.content, (t) => t.time.format("DD.MM.YYYY"));
        this.setState({
          entries: grouped
        });
      },
      error => {
        this.setState({
          error: error
        });
      });
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

    entries.forEach((v, k) => {
      views.push(<div key={"group-" + i}>
        <strong className="text-secondary">{k}</strong>
        {v.map(e => <HistoryEntryView entry={e} key={e.id}/>)};
      </div>);
      i += 1;
    });

    return <div>
      {error}
      {views}
    </div>;
  }
}

interface HistoryEntryViewProps {
  entry: HistoryEntry;
}

class HistoryEntryView extends React.Component<HistoryEntryViewProps, null> {
  render() {
    const entry = this.props.entry;
    const date = entry.time;
    const feedId = entry.feedItem.feed.id;
    const itemId = entry.feedItem.id;
    return <div>
      <Link to={`/app/podcasts/${feedId}/item/${itemId}`}>{entry.feedItem.title}</Link>&nbsp;
      <small><DateTimeComponent date={date} type={DisplayType.FromNow}/></small>
      <span className="float-right">{entry.feedItem.getFormattedElapsedTime()}</span>
    </div>;
  }
}
