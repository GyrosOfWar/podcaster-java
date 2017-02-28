import * as React from "react";
import * as ajax from "../common/ajax";
import Page from "../model/Page";
import HistoryEntry from "../model/HistoryEntry";
import Error from "../model/Error";
import DateTimeComponent, {DisplayType} from "../common/DateTimeComponent";

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

export default class Histoty extends React.Component<null, HistoryState> {
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
    if (this.state.error) {
      return <p>Error: {this.state.error.message}</p>;
    }
    const entries = this.state.entries;
    if (!entries) {
      return <p>...</p>;
    }
    
    const views: React.ReactElement<any>[] = [];
    entries.forEach((v, k) => {
      views.push(<div>
        <h2>{k}</h2>
        {v.map(e => <HistoryEntryView entry={e}/>)}
      </div>);
    });

    return <div>
      {views}
    </div>;
  }
}

interface HistoryEntryViewProps {
  entry: HistoryEntry;
}

class HistoryEntryView extends React.Component<HistoryEntryViewProps, null> {

  render() {
    const date = this.props.entry.time;

    return <div style={{"display": "inline-block"}}>
      <DateTimeComponent date={date} type={DisplayType.Time}/>: {this.props.entry.feedItem.title}
    </div>;
  }
}
