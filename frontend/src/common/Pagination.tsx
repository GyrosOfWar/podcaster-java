import * as React from "react";
import {Link} from "react-router";
import Page from "../model/Page";

interface PaginationProps {
  page: Page<any>;
  nextLink: string;
  prevLink: string;
}

export default class Pagination extends React.Component<PaginationProps, null> {
  constructor(props: PaginationProps) {
    super(props);
  }

  render() {
    const page = this.props.page;
    const hasPrevious = page.number > 0;
    const hasNext = page.number < page.totalElements - 1;

    return <div className="d-flex flex-row">
      {hasPrevious && <Link className="btn btn-outline-primary" to={this.props.prevLink}>Previous</Link>}
      {hasNext && <Link className="btn btn-outline-primary ml-auto" to={this.props.nextLink}>Next</Link>}
    </div>;
  }
}