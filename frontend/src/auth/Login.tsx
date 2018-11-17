import * as React from "react";
import * as auth from "../common/auth";
import Error from "../model/Error";
import { Alert, Form, FormGroup, Input, Label } from "reactstrap";

interface LoginState {
  error: Error | null;
  username: string;
  password: string;
}

export default class Login extends React.Component<any, LoginState> {
  constructor(props: any) {
    super(props);
    this.state = {
      error: null,
      username: "",
      password: ""
    };
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleSubmit(event: React.MouseEvent<HTMLButtonElement>) {
    event.preventDefault();
    const {username, password} = this.state;
    if (username !== null && password !== null) {
      auth.login(username, password,
          () => {
          this.props.history.push("/app/");
        },
        error => {
          this.setState({
            error: error
          });
        });
    }
  }

  onUsernameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    this.setState({
      username: e.target.value
    });
  }

  onPasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    this.setState({
      password: e.target.value
    });
  }

  render() {
    const {error, username, password} = this.state;

    return (
      <Form>
        {error && <Alert color="danger"><strong>{error.message}</strong></Alert>}
        <FormGroup>
          <Label for="username">Username</Label>
          <Input type="text" placeholder="Username" onChange={this.onUsernameChange} value={username}  />
        </FormGroup>
        <FormGroup>
          <Label for="password">Password</Label>
          <Input type="password" placeholder="Password" onChange={this.onPasswordChange} value={password} />
        </FormGroup>
        <button className="btn btn-primary" onClick={this.handleSubmit}>Login</button>
      </Form>
    );
  }
}