import * as React from "react";
import {Link} from "react-router";
import * as auth from "./common/auth";

export default class Navigation extends React.Component<null, null> {
    render() {
        return <nav>
            <ul>
                <li><h1>Podcaster</h1></li>
                <li><Link to="/">Home</Link></li>
                <li>{auth.isLoggedIn() ? <Link to="/logout">Logout</Link> : <Link to="/login">Login</Link>}</li>
            </ul>
        </nav>;
    }
}
