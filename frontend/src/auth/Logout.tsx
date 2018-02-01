import * as React from "react";
import * as auth from "../common/auth";
import { browserHistory } from "react-router";

export default class Logout extends React.Component<any, any> {
  componentDidMount() {
    auth.logout();
    browserHistory.push("/app");
  }

  render() {
    return <p />;
  }
}