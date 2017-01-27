import * as React from "react";
import Error from "../model/Error";
import * as ajax from "../common/ajax";
import FeedItem from "../model/FeedItem";
import Page from "../model/Page";

interface PodcastDetailsState {
    items?: Page<FeedItem>;
    error?: Error;
    currentPage: number;
}

export default class PodcastDetails extends React.Component<any, PodcastDetailsState> {
    constructor(props: any) {
        super(props);
        this.state = {
            currentPage: props.params.page,
        };
    }

    componentDidMount() {
        const id = this.props.params.id;

        ajax.getWithAuth(`/api/feeds/${id}/items?page=${this.state.currentPage}`,
            response => {
                const page = Page.fromJSON<FeedItem>(response);
                this.setState({
                    items: page
                });
            },
            error => {
                this.setState({error: error});
            });
    }

    render() {
        if (this.state.error) {
            return <div>{this.state.error.message}</div>;
        }

        if (!this.state.items) {
            return <div>...</div>;
        }

        return <div>
            {this.state.items.content.map(i => <PodcastDetailsItem item={i} key={i.id}/>)}
        </div>;
    }
}

interface PodcastDetailsItemProps {
    item: FeedItem;
}

class PodcastDetailsItem extends React.Component<PodcastDetailsItemProps, null> {
    render() {
        const item = this.props.item;
        return <div>
            <img src={item.getThumbnailUrl(120)}/>
            <h3>{item.title}</h3>
            {item.description}
        </div>;
    }
}