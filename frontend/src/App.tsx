import * as React from "react";
import {Router, Route, browserHistory, IndexRoute, RouterState, RedirectFunction, IndexRedirect} from "react-router";
import Login from "./auth/Login";
import Logout from "./auth/Logout";
import Navigation from "./Navigation";
import PodcastList from "./views/PodcastList";
import * as auth from "./common/auth";
import PodcastDetails from "./views/PodcastDetails";
import Player from "./player/Player";
import FeedItem from "./model/FeedItem";
import {postWithAuth} from "./common/ajax";
import * as moment from "moment";
import History from "./views/History";
import Error from "./model/Error";
import {Alert} from "reactstrap";

interface AppState {
  selectedItem?: FeedItem;
  error?: Error;
  lastSync?: moment.Moment;
}

class App extends React.Component<{}, AppState> {
  constructor(props: {}) {
    super(props);
    this.state = {};

    this.handleItemSelected = this.handleItemSelected.bind(this);
    this.updateItem = this.updateItem.bind(this);
    this.setItem = this.setItem.bind(this);
  }

  setItem(item: FeedItem) {

  }

  updateItem(item: FeedItem) {
    postWithAuth(`/api/feed_items/${item.id}`,
      JSON.stringify(item),
      () => {
        this.setState({
          lastSync: moment()
        });
      },
      error => {
        this.setState({
          error: error
        });
      });
  }

  handleItemSelected(item: FeedItem) {
    this.setState({selectedItem: item});
    const pos = item.lastPosition.asSeconds();
    const player = document.getElementById("player-audio") as HTMLAudioElement;
    if (player.currentTime !== pos) {
      player.currentTime = pos;
    }
    document.title = item.title;
  }

  componentDidMount() {
    document.title = "Podcaster";
  }

  render() {
    const children = React.Children.map(this.props.children, child => {
      return React.cloneElement(child as React.ReactElement<any>, {
        itemClicked: this.handleItemSelected
      });
    });

    const lastSync = this.state.lastSync;

    return (
      <div id="main" className="container">
        <Navigation />
        <div className="grow">
          {auth.isLoggedIn() &&
          <Player callbackInterval={10}
                  callbackHandler={this.updateItem}
                  setItem={this.setItem}
          />}
          {lastSync && <p>Last sync: {lastSync.fromNow()}</p>}
          {this.state.error && <Alert color="danger">{this.state.error.message}</Alert>}
          {children}
        </div>
      </div>
    );
  }
}

function requireAuth(nextState: RouterState, replace: RedirectFunction) {
  if (!auth.isLoggedIn()) {
    replace({
      pathname: "/app/login",
      state: {nextPathname: nextState.location.pathname}
    });
  }
}

class Routes extends React.Component<null, null> {
  render() {
    return <Router history={browserHistory}>
      <Route path="/" component={App}>
        <IndexRedirect to="/app"/>
        <Route path="app">
          <IndexRoute component={PodcastList} onEnter={requireAuth}/>
          <Route path="login" component={Login}/>
          <Route path="logout" component={Logout}/>
          <Route path="podcasts/:id/page/:page" component={PodcastDetails}/>
          <Route path="podcasts/:id/item/:itemId" component={PodcastDetails}/>
          <Route path="history" component={History}/>
        </Route>
      </Route>
    </Router>;
  }
}

export default Routes;
