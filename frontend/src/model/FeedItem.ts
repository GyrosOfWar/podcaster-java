import User from "./User";
import * as moment from "moment";

// const TAG_RE: RegExp = /<\/?(.*)\/?>/g.compile();

function removeTags(text: string): string {
  const tmp = document.createElement("div");
  tmp.innerHTML = text;
  return tmp.textContent || tmp.innerText || "";
}

export default class FeedItem {
  id: number;
  title: string;
  link: string;
  description: string;
  mp3Url: string;
  pubDate: moment.Moment;
  duration: moment.Duration;
  imageUrl: string;
  lastPosition: moment.Duration;
  owner: User;
  isFavorite: boolean;
  hashedImageUrl: String;

  static fromJSON(obj: any): FeedItem {
    return new FeedItem(
      obj.id,
      obj.title,
      obj.link,
      obj.description,
      obj.mp3Url,
      moment.unix(obj.pubDate),
      moment.duration(obj.duration, "seconds"),
      obj.imageUrl,
      moment.duration(obj.lastPosition, "seconds"),
      User.fromJSON(obj.owner),
      obj.isFavorite,
      obj.hashedImageUrl
    );
  }

  constructor(id: number, title: string, link: string, description: string, mp3Url: string,
              pubDate: moment.Moment, duration: moment.Duration, imageUrl: string, lastPosition: moment.Duration,
              owner: User, isFavorite: boolean, hashedImageUrl: String) {
    this.id = id;
    this.title = title;
    this.link = link;
    this.description = description;
    this.mp3Url = mp3Url;
    this.pubDate = pubDate;
    this.duration = duration;
    this.imageUrl = imageUrl;
    this.lastPosition = lastPosition;
    this.owner = owner;
    this.isFavorite = isFavorite;
    this.hashedImageUrl = hashedImageUrl;
  }

  getThumbnailUrl(size: number): string {
    return "/api/images/" + this.hashedImageUrl + "?size=" + size;
  }

}