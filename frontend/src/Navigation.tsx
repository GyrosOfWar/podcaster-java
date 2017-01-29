import * as React from "react";
import {Link} from "react-router";
import * as auth from "./common/auth";
import "./styles/nav.css";

export default class Navigation extends React.Component<null, null> {
    render() {
        return <aside className="menu">
            <big><p className="menu-label">Podcaster</p></big>
            <ul className="menu-list">
                <li><Link to="/">Home</Link></li>
                <li>{auth.isLoggedIn() ? <Link to="/logout">Logout</Link> : <Link to="/login">Login</Link>}</li>
            </ul>
        </aside>;
    }
}
