import * as React from "react";
import * as moment from "moment";

export enum DisplayType {
  FromNow,
  DateTime,
  Date,
  Time
}

interface DateTimeComponentProps {
  date: moment.Moment | string;
  type?: DisplayType;
}

export default class DateTimeComponent extends React.Component<DateTimeComponentProps, {}> {
  render() {
    const date = typeof this.props.date === "string" ? moment(this.props.date) : this.props.date;
    const type = this.props.type;
    let fmt;
    const long = date.format();

    switch (type) {
      case DisplayType.FromNow:
        fmt = date.fromNow();
        break;
      case DisplayType.DateTime:
        fmt = date.format("DD.MM.YY HH:mm");
        break;
      case DisplayType.Date:
        fmt = date.format("DD.MM.YY");
        break;
      case DisplayType.Time:
        fmt = date.format("HH:mm");
        break;
    }

    return <time title={long} dateTime={long}>{fmt}</time>;
  }
}