import User from "./User";
import * as moment from "moment";

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

    constructor(...args: any[]) {

    }

    getThumbnailUrl(size: number): string {
        return "/api/images/" + this.hashedImageUrl + "?size=" + size;
    }

}