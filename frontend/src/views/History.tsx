import * as React from "react";
import * as ajax from "../common/ajax";
import Page from "../model/Page";
import HistoryEntry from "../model/HistoryEntry";
import Error from "../model/Error";

interface HistoryState {
  entries?: Page<HistoryEntry>;
  error?: Error;
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
        this.setState({
          entries: page
        });
      },
      error => {
        this.setState({
          error: Error.fromJSON(error)
        });
      });
  }

  render() {
    if (this.state.error) {
      return <p>Error: {this.state.error.message}</p>;
    }
    if (!this.state.entries) {
      return <p>...</p>;
    }

    return <div>
      {this.state.entries.content.map(e => <HistoryEntryView entry={e}/>)}
    </div>;
  }
}

interface HistoryEntryViewProps {
  entry: HistoryEntry;
}

class HistoryEntryView extends React.Component<HistoryEntryViewProps, null> {
  render() {
    return <div>
      {this.props.entry.time.format()}: {this.props.entry.feedItem.title}
    </div>;
  }
}
