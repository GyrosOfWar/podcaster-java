import User from "./User";
import * as moment from "moment";
import RssFeed from "./RssFeed";

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
  hashedImageUrl: string;
  feed: RssFeed;
}

export function parseDates(item: FeedItem): void {
  if (typeof item.duration === "number") {
    item.duration = moment.duration(item.duration, "seconds");
  }

  if (typeof item.lastPosition === "number") {
    item.lastPosition = moment.duration(item.lastPosition, "seconds");
  }

  if (typeof item.pubDate === "string") {
    item.pubDate = moment(item.pubDate);
  }
}