import * as React from "react";
import * as moment from "moment";

export enum DisplayType {
  FromNow,
  DateTime,
  Date,
  Time
}

interface DateTimeComponentProps {
  date: moment.Moment;
  type?: DisplayType;
}

export default class DateTimeComponent extends React.Component<DateTimeComponentProps, null> {
  render() {
    const date = this.props.date;
    const type = this.props.type || DisplayType.DateTime;
    let fmt;

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

    return <time title={date.format()} dateTime={date.format()}>{fmt}</time>;
  }
}