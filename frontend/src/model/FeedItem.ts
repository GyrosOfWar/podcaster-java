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

    static fromJSON(obj: any): FeedItem {
        return new FeedItem(
            obj.id,
            obj.title,
            obj.link,
            obj.description,
            obj.mp3Url,
            moment(obj.pubDate),
            moment.duration(obj.duration),
            obj.imageUrl,
            moment.duration(obj.lastPosition),
            User.fromJSON(obj.user),
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

    getThumbnailUrl(): string {
        return "/api/images/" + this.hashedImageUrl;
    }

}