import * as React from "react";
import * as auth from "./common/auth";
import {Navbar, Collapse, NavbarToggler, Nav, NavItem} from "reactstrap";
import {Link, browserHistory} from "react-router";

interface SearchBoxProps {
}

class SearchBox extends React.Component<SearchBoxProps, null> {
  constructor(props: SearchBoxProps) {
    super(props);
    this.state = null;

    this.onInput = this.onInput.bind(this);
  }

  onInput(event: React.FocusEvent<HTMLInputElement>) {
    const text = event.currentTarget.value.replace(/\s/g, "&");
    if (text) {
      browserHistory.push("/app/search?q=" + encodeURIComponent(text));
    }

    if (!text) {
      browserHistory.goBack();
    }
  }

  render() {
    return <input className="form-control mr-sm-2" type="search" placeholder="Search" onBlur={this.onInput}/>;
  }
}

interface NavigationState {
  isOpen: boolean;
}

interface NavigationProps {
}

export default class Navigation extends React.Component<NavigationProps, NavigationState> {
  constructor(props: NavigationProps) {
    super(props);

    this.toggle = this.toggle.bind(this);
    this.state = {
      isOpen: false
    };
  }

  toggle() {
    this.setState({
      isOpen: !this.state.isOpen
    });
  }

  render() {
    return (
      <div>
        <Navbar light color="primary" toggleable>
          <NavbarToggler right onClick={this.toggle}/>
          <Link className="navbar-brand" to="/app/">Podcaster</Link>
          <Collapse isOpen={this.state.isOpen} navbar>
            <Nav navbar className="mr-auto">
              <NavItem>
                <Link className="nav-link" to="/app/history">History</Link>
              </NavItem>
              <NavItem>
                {auth.isLoggedIn() ?
                  <Link className="nav-link" to="/app/logout">Logout</Link> :
                  <Link className="nav-link" to="/app/login">Login</Link>}
              </NavItem>
            </Nav>
            <form className="form-inline my-2 my-lg-0">
              <SearchBox/>
            </form>
          </Collapse>
        </Navbar>
      </div>
    );
  }
}
