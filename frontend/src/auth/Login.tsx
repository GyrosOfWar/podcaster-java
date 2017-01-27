import * as React from "react";
import * as auth from "../common/auth";
import "../styles/forms.css";
import {browserHistory} from "react-router";
import Error from "../model/Error";

interface LoginState {
    error: Error | null;
}

export default class Login extends React.Component<any, LoginState> {
    username: HTMLInputElement;
    password: HTMLInputElement;

    constructor(props: any) {
        super(props);
        this.state = {
            error: null
        };
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();
        const username = this.username.value;
        const password = this.password.value;
        if (username !== null && password !== null) {
            auth.login(username, password,
                response => {
                    browserHistory.push("/");
                },
                error => {
                    this.setState({
                        error: error
                    });
                });
        }
    }

    render() {
        let error = null;
        if (this.state.error) {
            error = <span className="form-error">{this.state.error.message}</span>;
        }

        return <form className="form" onSubmit={this.handleSubmit}>
            {error}
            <div className="form-group">
                <label className="form-label" htmlFor="username">Username:</label>
                <input className="text-input" type="text" id="username" ref={(el) => this.username = el}/>
            </div>
            <div className="form-group">
                <label className="form-label" htmlFor="password">Password</label>
                <input className="text-input" type="password" id="password" ref={(el) => this.password = el}/>
            </div>
            <div className="form-group">
                <button className="button">Login</button>
            </div>
        </form>;
    }
}