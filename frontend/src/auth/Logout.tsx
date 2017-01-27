import * as React from "react";
import * as auth from "../common/auth";
import "../styles/forms.css";
import {browserHistory} from "react-router";

export default class Logout extends React.Component<any, any> {
    componentDidMount() {
        auth.logout();
        browserHistory.push("/");
    }

    render() {
        return <p/>;
    }
}