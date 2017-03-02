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

    return <div className="pagination">
      <span>
        {hasPrevious && <Link className="button button-outline" to={this.props.prevLink}>Previous</Link>}
        {hasNext && <Link className="button button-outline pull-right" to={this.props.nextLink}>Next</Link>}
      </span>
    </div>;
  }
}