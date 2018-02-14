import * as React from "react";
import * as auth from "./common/auth";
import { Collapse, Nav, Navbar, NavbarToggler, NavItem } from "reactstrap";
import { browserHistory, Link } from "react-router";

interface SearchBoxProps {

}

interface SearchBoxState {
  search: string;
}

class SearchBox extends React.Component<SearchBoxProps, SearchBoxState> {
  constructor(props: SearchBoxProps) {
    super(props);
    this.state = {
      search: ""
    };

    this.onInput = this.onInput.bind(this);
    this.onSearch = this.onSearch.bind(this);
    this.onKeyDown = this.onKeyDown.bind(this);
  }

  onSearch(event?: React.MouseEvent<HTMLButtonElement>) {
    if (event) {
      event.preventDefault();
    }
    browserHistory.push("/app/search?q=" + encodeURIComponent(this.state.search));
  }

  onKeyDown(event: React.KeyboardEvent<HTMLInputElement>) {
    if (event.key === "Enter") {
      this.onSearch();
    }
  }

  onInput(event: React.ChangeEvent<HTMLInputElement>) {
    const text = event.currentTarget.value.replace(/\s/g, "&").trim();
    this.setState({
      search: text
    });
  }

  render() {
    return (
      <form className="form-inline my-2 my-lg-0">
        <input
          className="form-control mr-sm-2"
          type="search"
          placeholder="Search"
          onChange={this.onInput}
          onKeyDown={this.onKeyDown}
        />
        <button
          className="btn btn-outline-success my-2 my-sm-0"
          onClick={this.onSearch}
          type="submit"
        >
          Search
        </button>
      </form>
    );
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
        <Navbar light={true} color="light" expand={true}>
          <NavbarToggler onClick={this.toggle} />
          <Link className="navbar-brand" to="/app/">Podcaster</Link>
          <Collapse isOpen={this.state.isOpen} navbar={true}>
            <Nav navbar={true} className="mr-auto">
              {auth.isLoggedIn() &&
              <NavItem>
                <Link className="nav-link" to="/app/history">History</Link>
              </NavItem>
              }
              <NavItem>
                {auth.isLoggedIn() ?
                  <Link className="nav-link" to="/app/logout">Logout</Link> :
                  <Link className="nav-link" to="/app/login">Login</Link>}
              </NavItem>
            </Nav>
            <SearchBox />
          </Collapse>
        </Navbar>
      </div>
    );
  }
}
