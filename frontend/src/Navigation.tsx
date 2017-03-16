import * as React from "react";
import * as auth from "./common/auth";
import {Navbar, Collapse, NavbarBrand, NavbarToggler, Nav, NavLink, NavItem} from "reactstrap";

interface NavigationState {
  isOpen: boolean;
}

export default class Navigation extends React.Component<null, NavigationState> {
  constructor(props: null) {
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
        <Navbar color="primary" toggleable>
          <NavbarToggler right onClick={this.toggle}/>
          <NavbarBrand href="/">Podcaster</NavbarBrand>
          <Collapse isOpen={this.state.isOpen} navbar>
            <Nav className="ml-auto" navbar>
              <NavItem>
                <NavLink href="/app/history">History</NavLink>
              </NavItem>
              <NavItem>
                {auth.isLoggedIn() ?
                  <NavLink href="/app/logout">Logout</NavLink> :
                  <NavLink href="/app/login">Login</NavLink>}
              </NavItem>
            </Nav>
          </Collapse>
        </Navbar>
      </div>
    );
  }
}
