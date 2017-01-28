import * as React from "react";
import Error from "../model/Error";
import * as ajax from "../common/ajax";
import FeedItem from "../model/FeedItem";
import Page from "../model/Page";
import Player from "../player/Player";

interface PodcastDetailsState {
    items?: Page<FeedItem>;
    error?: Error;
    currentPage: number;
    selectedItem?: FeedItem;
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
                const page = Page.fromJSON(response, FeedItem.fromJSON);
                this.setState({
                    items: page
                });
            },
            error => {
                this.setState({error: error});
            });
    }

    handleItemClicked(item: FeedItem) {
        this.setState({
            selectedItem: item
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
            <Player item={this.state.selectedItem}/>
            {this.state.items.content.map(i =>
                <PodcastDetailsItem item={i} key={i.id} itemClicked={this.handleItemClicked.bind(this)}/>)}
        </div>;
    }
}

interface PodcastDetailsItemProps {
    item: FeedItem;
    itemClicked: (i: FeedItem) => void;
}

class PodcastDetailsItem extends React.Component<PodcastDetailsItemProps, null> {
    clickItem(event: React.FormEvent<HTMLButtonElement>) {
        this.props.itemClicked(this.props.item);
    }

    render() {
        const item = this.props.item;
        return <div className="podcast-details-item">
            <div className="row with-margins">
                <img className="podcast-details-image" src={item.getThumbnailUrl(120)}/>
                <div className="podcast-title">{item.title}</div>
            </div>
            <div className="row with-margins">
                <div className="podcast-details-description">{item.description}</div>
                <div className="buttons">
                    <button onClick={this.clickItem.bind(this)} className="button-small">Play</button>
                </div>
            </div>
        </div>;
    }
}