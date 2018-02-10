import * as moment from "moment";
import User from "./User";
import * as F from "./FeedItem";
import FeedItem from "./FeedItem";

export default interface Bookmark {
  id: number;
  position: moment.Duration | string;
  user: User;
  feedItem: FeedItem;
}

export function parseDates(bookmark: Bookmark): void {
  if (typeof bookmark.position === "string") {
    bookmark.position = moment.duration(bookmark.position);
  }
  F.parseDates(bookmark.feedItem);
}