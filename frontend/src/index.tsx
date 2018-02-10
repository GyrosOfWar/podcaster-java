import * as React from "react";
import * as ReactDOM from "react-dom";
import Routes from "./App";
import "./style/bootstrap.min.css";
import "../node_modules/font-awesome/css/font-awesome.min.css";
import "./style/custom.css";

ReactDOM.render(<Routes />, document.getElementById("root") as HTMLElement);
