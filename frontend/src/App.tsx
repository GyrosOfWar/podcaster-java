import * as React from "react";
import {Router, Route, browserHistory, IndexRoute, RouterState, RedirectFunction} from "react-router";
import Login from "./auth/Login";
import Logout from "./auth/Logout";
import Navigation from "./Navigation";
import PodcastList from "./views/PodcastList";
import * as auth from "./common/auth";
import PodcastDetails from "./views/PodcastDetails";
import Player from "./player/Player";
import FeedItem from "./model/FeedItem";
import "./styles/base.css";
import "../node_modules/milligram/dist/milligram.css";
import {postWithAuth} from "./common/ajax";

interface AppState {
  selectedItem?: FeedItem;
}

function updateItem(item: FeedItem) {
  postWithAuth(`/api/feed_items/${item.id}`,
      JSON.stringify(item),
      result => {

      },
      error => {

      });
}

class App extends React.Component<{}, AppState> {
  constructor(props: {}) {
    super(props);
    this.state = {};
    this.handleItemSelected = this.handleItemSelected.bind(this);
  }

  handleItemSelected(item: FeedItem) {
    this.setState({selectedItem: item});
  }

  render() {
    const children = React.Children.map(this.props.children, child => {
      return React.cloneElement(child as React.ReactElement<any>, {
        itemClicked: this.handleItemSelected
      });
    });
    return (
        <div id="main" className="container">
          <Navigation />
          <div className="grow">
            <Player callbackInterval={15} callbackHandler={updateItem} item={this.state.selectedItem}/>
            {children}
          </div>
        </div>
    );
  }
}

function requireAuth(nextState: RouterState, replace: RedirectFunction) {
  if (!auth.isLoggedIn()) {
    replace({
      pathname: "/login",
      state: {nextPathname: nextState.location.pathname}
    });
  }
}

class Routes extends React.Component<null, null> {
  render() {
    return <Router history={browserHistory}>
      <Route path="/" component={App}>
        <IndexRoute component={PodcastList} onEnter={requireAuth}/>
        <Route path="/login" component={Login}/>
        <Route path="/logout" component={Logout}/>
        <Route path="/podcasts/:id/page/:page" component={PodcastDetails}/>
      </Route>
    </Router>;
  }
}

export default Routes;
