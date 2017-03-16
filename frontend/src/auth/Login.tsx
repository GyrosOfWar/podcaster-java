import * as React from "react";
import * as auth from "../common/auth";
import {browserHistory} from "react-router";
import Error from "../model/Error";
import {Form, FormGroup, Alert, Input, Label} from "reactstrap";

interface LoginState {
  error: Error | null;
}

export default class Login extends React.Component<any, LoginState> {

  constructor(props: any) {
    super(props);
    this.state = {
      error: null
    };
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleSubmit(event: React.MouseEvent<HTMLButtonElement>) {
    event.preventDefault();
    const username = (document.getElementById("username") as HTMLInputElement).value;
    const password = (document.getElementById("password") as HTMLInputElement).value;
    if (username !== null && password !== null) {
      auth.login(username, password,
        response => {
          browserHistory.push("/app/");
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
      error = <Alert color="danger"><strong>{this.state.error.message}</strong></Alert>;
    }

    return <Form>
      {error}
      <FormGroup>
        <Label for="username">Username</Label>
        <Input type="text" name="username" id="username" placeholder="Username"/>
      </FormGroup>
      <FormGroup>
        <Label for="password">Password</Label>
        <Input type="password" name="password" id="password" placeholder="Password"/>
      </FormGroup>
      <button className="btn" onClick={this.handleSubmit}>Login</button>
    </Form>;

    // return <form className="form" onSubmit={this.handleSubmit}>
    //   {error}
    //   <div className="form-group">
    //     <label className="form-label" htmlFor="username">Username:</label>
    //     <input className="text-input" type="text" id="username" ref={(el) => this.username = el}/>
    //   </div>
    //   <div className="form-group">
    //     <label className="form-label" htmlFor="password">Password</label>
    //     <input className="text-input" type="password" id="password" ref={(el) => this.password = el}/>
    //   </div>
    //   <div className="form-group">
    //     <button className="button button-outline">Login</button>
    //   </div>
    // </form>;
  }
}