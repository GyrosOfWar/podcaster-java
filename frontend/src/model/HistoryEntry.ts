import * as F from "./FeedItem";
import FeedItem from "./FeedItem";
import User from "./User";
import * as moment from "moment";

export default interface HistoryEntry {
  feedItem: FeedItem;
  user: User;
  time: string | moment.Moment;
  id: number;
}

export function parseDates(entry: HistoryEntry) {
  if (typeof entry.time === "string") {
    entry.time = moment(entry.time);
  }

  F.parseDates(entry.feedItem);
}