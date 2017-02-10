import * as React from "react";
import {Link} from "react-router";
import * as auth from "./common/auth";
import "./styles/nav.css";

export default class Navigation extends React.Component<null, null> {
  render() {
    return <aside className="menu">
      <big><p className="menu-label">Podcaster</p></big>
      <ul className="menu-list">
        <li><Link to="/app">Home</Link></li>
        <li>{auth.isLoggedIn() ? <Link to="/app/logout">Logout</Link> : <Link to="/app/login">Login</Link>}</li>
      </ul>
    </aside>;
  }
}
