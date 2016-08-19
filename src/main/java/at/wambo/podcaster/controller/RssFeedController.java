package at.wambo.podcaster.controller;

import at.wambo.podcaster.configuration.CurrentUser;
import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.RssFeed;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.repository.FeedItemRepository;
import at.wambo.podcaster.repository.RssFeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Martin
 *         01.07.2016
 */
@RestController
@RequestMapping(path = "/api/feeds")
public class RssFeedController {
    private static final int MAX_COUNT = 30;
    private static final Random RANDOM = new Random();

    @Autowired
    private RssFeedRepository feedRepository;
    @Autowired
    private FeedItemRepository feedItemRepository;

    private User getUser() {
        return ((CurrentUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

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
    public RssFeed addFeed(@RequestParam(value = "url") String url) {
        User user = getUser();
        RssFeed feed = RssFeed.fromUrl(url);
        feed.setOwner(user);
        RssFeed savedFeed = this.feedRepository.save(feed);
        // See above
        savedFeed.setItems(Collections.emptyList());

        return savedFeed;
    }

    @RequestMapping(path = "{feed}", method = RequestMethod.GET)
    public RssFeed feedWithId(@PathVariable RssFeed feed) {
        if (feed != null) {
            feed.setItems(Collections.emptyList());
            return feed;
        } else {
            return null;
        }
    }

    @RequestMapping(path = "{feed}", method = RequestMethod.POST)
    public List<FeedItem> refresh(@PathVariable RssFeed feed) {
        if (feed != null) {
            return feed.refresh(this.feedItemRepository);
        } else {
            return null;
        }
    }

    @RequestMapping(path = "{feedId}/{offset}/{count}")
    public List<FeedItem> getPaginated(@PathVariable Integer feedId, @PathVariable Integer offset, @PathVariable Integer count) {
        return feedItemRepository.findByFeedIdPaginated(feedId, offset, count);
    }

    @RequestMapping(path = "{feed}/search", method = RequestMethod.GET)
    public List<FeedItem> searchItems(@PathVariable RssFeed feed, @RequestParam(value = "query") String query) {
        String tsQuery = query.trim().replace(' ', '&');
        return this.feedRepository.fullTextSearch(feed.getId(), tsQuery);
    }

    @RequestMapping(path = "{feed}/random")
    public FeedItem randomItem(@PathVariable RssFeed feed) {
        int max = feed.getItems().size();
        return feed.getItems().get(RANDOM.nextInt(max));
    }

    @RequestMapping(path = "{feed}/favorites")
    public List<FeedItem> favorites(@PathVariable RssFeed feed) {
        return feedRepository.findByItemsIsFavoriteTrueAndId(feed.getId());
    }
}
