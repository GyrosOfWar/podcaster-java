import FeedItem from "./FeedItem";
import User from "./User";
import * as moment from "moment";

export default class HistoryEntry {
    feedItem: FeedItem;
    user: User;
    time: moment.Moment;
    id: number;
}