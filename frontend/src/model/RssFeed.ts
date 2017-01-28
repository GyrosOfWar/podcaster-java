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

    getThumbnailUrl(size: number): string {
        return "/api/images/" + this.hashedImageUrl + "?size=" + size;
    }
}