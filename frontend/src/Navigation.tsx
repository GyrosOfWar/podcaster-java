import * as React from "react";
import {Link} from "react-router";

export default class Navigation extends React.Component<null, null> {
    render() {
        return <nav>
            <ul>
                <li><h1>Podcaster</h1></li>
                <li><Link to="/">Home</Link></li>
            </ul>
        </nav>
    }
}
