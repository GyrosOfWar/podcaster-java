import FeedItem from "./FeedItem";
import User from "./User";
import * as moment from "moment";

export default class HistoryEntry {
    feedItem: FeedItem;
    user: User;
    time: moment.Moment;
    id: number;

    constructor(feedItem: FeedItem, user: User, time: moment.Moment, id: number) {
        this.feedItem = feedItem;
        this.user = user;
        this.time = time;
        this.id = id;
    }

    static fromJSON(obj: any): HistoryEntry {
        return new HistoryEntry(
            FeedItem.fromJSON(obj.feedItem),
            User.fromJSON(obj.user),
            moment(obj.time),
            obj.id
        )
    }
}