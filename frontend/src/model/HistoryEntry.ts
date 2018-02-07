import FeedItem from "./FeedItem";
import User from "./User";

export default interface HistoryEntry {
  feedItem: FeedItem;
  user: User;
  time: number;
  id: number;
}