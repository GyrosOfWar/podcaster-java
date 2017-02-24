import * as React from "react";
import {Link} from "react-router";
import * as auth from "./common/auth";
import "./styles/nav.css";

export default class Navigation extends React.Component<null, null> {
  render() {
    return <aside className="menu">
      <big><Link to="/app"><p className="menu-label">Podcaster</p></Link></big>
      <ul className="menu-list">
        <li><Link to="/app/history">History</Link></li>
        <li>{auth.isLoggedIn() ? <Link to="/app/logout">Logout</Link> : <Link to="/app/login">Login</Link>}</li>
      </ul>
    </aside>;
  }
}
