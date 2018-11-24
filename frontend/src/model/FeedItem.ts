import User from "./User";
import * as moment from "moment";
import RssFeed from "./RssFeed";
import Bookmark from "./Bookmark";

export default interface FeedItem {
  id: number;
  title: string;
  link: string;
  description: string;
  mp3Url: string;
  pubDate: moment.Moment | string;
  duration: moment.Duration | string;
  imageUrl: string;
  lastPosition: moment.Duration | string;
  owner: User;
  favorite: boolean;
  feed: RssFeed;
  bookmarks: Array<Bookmark>;
  guid: string;
}

export function parseDates(item: FeedItem): void {
  if (typeof item.duration === "string") {
    item.duration = moment.duration(item.duration);
  }

  if (typeof item.lastPosition === "string") {
    item.lastPosition = moment.duration(item.lastPosition);
  }

  if (typeof item.pubDate === "string") {
    item.pubDate = moment(item.pubDate);
  }
}