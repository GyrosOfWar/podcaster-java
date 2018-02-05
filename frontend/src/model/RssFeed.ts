import User from "./User";
import FeedItem from "./FeedItem";

export default interface RssFeed {
  id: number;
  feedUrl: string;
  title: string;
  imageUrl: string;
  owner: User;
  items: Array<FeedItem>;
  hashedImageUrl: string;
}