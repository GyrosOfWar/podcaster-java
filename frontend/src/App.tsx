import * as React from "react";
import { browserHistory, IndexRedirect, IndexRoute, RedirectFunction, Route, Router, RouterState } from "react-router";
import Login from "./auth/Login";
import Logout from "./auth/Logout";
import Navigation from "./Navigation";
import PodcastList from "./views/PodcastList";
import * as auth from "./common/auth";
import PodcastDetails from "./views/PodcastDetails";
import Player from "./player/Player";
import FeedItem from "./model/FeedItem";
import fetchWithAuth from "./common/ajax";
import * as moment from "moment";
import History from "./views/History";
import Error from "./model/Error";
import { Alert } from "reactstrap";
import SearchResults from "./views/SearchResults";

interface AppState {
  selectedItem?: FeedItem;
  error?: Error;
  lastSync?: moment.Moment;
  searchQuery?: string;
}

class App extends React.Component<{}, AppState> {
  constructor(props: {}) {
    super(props);
    this.state = {};

    this.handleItemSelected = this.handleItemSelected.bind(this);
    this.updateItem = this.updateItem.bind(this);
    this.searchCallback = this.searchCallback.bind(this);
    this.searchEnded = this.searchEnded.bind(this);
    this.itemChanged = this.itemChanged.bind(this);
  }

  async updateItem(item: FeedItem) {
    try {
      await fetchWithAuth(`/api/feed_items/${item.id}`, {
        method: "POST",
        body: JSON.stringify(item)
      });
      this.setState({
        lastSync: moment()
      });
    } catch (error) {
      this.setState({ error });
    }
  }

  searchCallback(query: string) {
    this.setState({
      searchQuery: query
    });
  }

  searchEnded() {
    this.setState({
      searchQuery: undefined
    });
  }

  handleItemSelected(item: FeedItem) {
    this.setState({ selectedItem: item });
    let pos;
    if (typeof item.lastPosition === "string") {
      pos = moment.duration(item.lastPosition).asSeconds();
    } else {
      pos = item.lastPosition.asSeconds();
    }
    const player = document.getElementById("player-audio") as HTMLAudioElement;
    if (player.currentTime !== pos) {
      player.currentTime = pos;
    }
    document.title = item.title;
  }

  componentDidMount() {
    document.title = "Podcaster";
  }

  itemChanged(item: FeedItem) {
    this.setState({
      selectedItem: item
    });
  }

  render() {
    const children = React.Children.map(this.props.children, child => {
      return React.cloneElement(child as React.ReactElement<any>, {
        itemClicked: this.handleItemSelected
      });
    });
    return (
      <React.Fragment>
        <Navigation />
        <main id="main" className="container">
          {auth.isLoggedIn() &&
          <Player
            callbackInterval={10}
            callbackHandler={this.updateItem}
            item={this.state.selectedItem}
            itemChanged={this.itemChanged}
          />
          }
          {this.state.error && <Alert color="danger">{this.state.error.message}</Alert>}
          {children}
        </main>
      </React.Fragment>
    );
  }
}

function requireAuth(nextState: RouterState, replace: RedirectFunction) {
  if (!auth.isLoggedIn()) {
    replace({
      pathname: "/app/login",
      state: { nextPathname: nextState.location.pathname }
    });
  }
}

const NotFound = () => <p>Resource not found (404)</p>;

class Routes extends React.Component<any, any> {
  render() {
    return (
      <Router history={browserHistory}>
        <Route path="/" component={App}>
          <IndexRedirect to="/app" />
          <Route path="app">
            <IndexRoute component={PodcastList} onEnter={requireAuth} />
            <Route path="login" component={Login} />
            <Route path="logout" component={Logout} />
            <Route path="podcasts/:id" component={PodcastDetails} onEnter={requireAuth} />
            <Route path="podcasts/:id/item/:itemId" component={PodcastDetails} onEnter={requireAuth} />
            <Route path="history" component={History} onEnter={requireAuth} />
            <Route path="search" component={SearchResults} onEnter={requireAuth} />
          </Route>
        </Route>
        <Route path="*" component={NotFound} />
      </Router>);
  }
}

export default Routes;
