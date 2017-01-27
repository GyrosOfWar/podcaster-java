import * as React from "react";
import {Router, Route, browserHistory, IndexRoute, RouterState, RedirectFunction} from "react-router";
import Login from "./auth/Login";
import Logout from "./auth/Logout";
import "./styles/base.css";
import "./styles/nav.css";
import Navigation from "./Navigation";
import PodcastList from "./views/PodcastList";
import * as auth from "./common/auth";
import PodcastDetails from "./views/PodcastDetails";

class App extends React.Component<{}, {}> {
  render() {
    return (
        <div id="main">
          <Navigation />
          {this.props.children}
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
