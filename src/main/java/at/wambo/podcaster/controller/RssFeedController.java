package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.RssFeed;
import at.wambo.podcaster.repository.FeedItemRepository;
import at.wambo.podcaster.repository.RssFeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Martin
 *         01.07.2016
 */
@RestController
@RequestMapping(path = "/api/feeds")
public class RssFeedController {
    @Autowired
    private RssFeedRepository feedRepository;
    @Autowired
    private FeedItemRepository feedItemRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<RssFeed> getFeeds() {
        List<RssFeed> feeds = new ArrayList<>();
        // Ugly solution, fix with lazy loading
        this.feedRepository.findAll().forEach(f -> {
            f.setItems(Collections.emptyList());
            feeds.add(f);
        });
        return feeds;
    }

    @RequestMapping(method = RequestMethod.POST)
    public RssFeed addFeed(@RequestBody String url) {
        RssFeed feed = RssFeed.fromUrl(url);
        RssFeed savedFeed = this.feedRepository.save(feed);
        // See above
        savedFeed.setItems(Collections.emptyList());

        return savedFeed;
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public RssFeed feedWithId(@PathVariable Integer id) {
        RssFeed result = this.feedRepository.findOne(id);
        if (result == null) {
            return null;
        }
        result.setItems(Collections.emptyList());
        return result;
    }

    @RequestMapping(path = "{id}", method = RequestMethod.POST)
    public List<FeedItem> refresh(@PathVariable Integer id) {
        RssFeed feed = this.feedRepository.findOne(id);
        if (feed != null) {
            return feed.refresh(this.feedItemRepository);
        } else {
            return null;
        }
    }

    @RequestMapping(path = "{id}/{offset}/{count}")
    public Page<FeedItem> getPaginated(@PathVariable Integer id, @PathVariable Integer offset, @PathVariable Integer count) {
        // this.feedItemRepository.findByFeed(id)
        //this.feedItemRepository.find
        return null;
    }

}
