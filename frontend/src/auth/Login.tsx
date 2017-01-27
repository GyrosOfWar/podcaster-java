import * as React from 'react';
import * as auth from "../auth/auth";

interface LoginState {
    error: string | null
}

export default class Login extends React.Component<any, LoginState> {
    username: HTMLInputElement;
    password: HTMLInputElement;

    constructor(props: any) {
        super(props);
        this.state = {
            error: null
        };
    }

    handleSubmit(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();
        const username = this.username.value;
        const password = this.password.value;
        auth.login(username, password, response => {

        }, error => {

        })
    }

    render() {
        return <form onSubmit={this.handleSubmit}>
            <label htmlFor="username">Username:</label>
            <input type="text" id="username" ref={(el) => this.username = el} />

            <label htmlFor="password">Password</label>
            <input type="password" id="password" ref={(el) => this.password = el} />
        </form>
    }
}