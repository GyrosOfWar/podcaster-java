import User from "./User";
import FeedItem from "./FeedItem";

export default class RssFeed {
    id: number;
    feedUrl: string;
    title: string;
    imageUrl: string;
    owner: User;
    items: Array<FeedItem>;
    hashedImageUrl: string;

    constructor(id: number, feedUrl: string, title: string, imageUrl: string, owner: User, items: Array<FeedItem>, hashedImageUrl: string) {
        this.id = id;
        this.feedUrl = feedUrl;
        this.title = title;
        this.imageUrl = imageUrl;
        this.owner = owner;
        this.items = items;
        this.hashedImageUrl = hashedImageUrl;
    }

    static fromJSON(obj: any): RssFeed {
        return new RssFeed(
            obj.id,
            obj.feedUrl,
            obj.title,
            obj.imageUrl,
            User.fromJSON(obj.owner),
            obj.items ? obj.items.map((i: any) => FeedItem.fromJSON(i)) : [],
            obj.hashedImageUrl
        );
    }
}