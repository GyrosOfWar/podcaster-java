package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.RssFeed;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.repository.FeedItemRepository;
import at.wambo.podcaster.repository.RssFeedRepository;
import at.wambo.podcaster.service.HistoryService;
import at.wambo.podcaster.util.Util;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author Martin
 *         01.07.2016
 */
@RestController
@RequestMapping(path = "/api/feeds")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RssFeedController {
    private static final int MAX_COUNT = 30;
    private static final Random RANDOM = new Random();

    private final @NonNull RssFeedRepository feedRepository;
    private final @NonNull FeedItemRepository feedItemRepository;
    private final @NonNull HistoryService historyService;

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<RssFeed> getFeeds() {
        return this.feedRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public RssFeed addFeed(@RequestParam(value = "url") String url) {
        User user = Util.getUser();
        RssFeed feed = RssFeed.fromUrl(url, user);
        return this.feedRepository.save(feed);
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

    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public String deleteFeed(@PathVariable Integer id) {
        Optional<RssFeed> result = feedRepository.findById(id);
        if (result.isPresent()) {
            RssFeed feed = result.get();
            historyService.deleteForFeed(id);
            feedItemRepository.delete(feed.getItems());
            feedRepository.deleteById(id);
            return "OK";
        } else {
            return "Feed doesn't exist";
        }
    }

    @RequestMapping(path = "{feed}", method = RequestMethod.POST)
    public List<FeedItem> refresh(@PathVariable RssFeed feed) {
        if (feed != null) {
            User user = Util.getUser();
            return feed.refresh(this.feedItemRepository, user);
        } else {
            return null;
        }
    }

    @RequestMapping(path = "{feedId}/items")
    public Page<FeedItem> getPaginated(@PathVariable Integer feedId, Pageable pageable) {
        return this.feedItemRepository.findByFeedIdOrderByPubDateDesc(feedId, pageable);
    }

    @RequestMapping(path = "{feed}/search", method = RequestMethod.GET)
    public List<FeedItem> searchItems(@PathVariable RssFeed feed, @RequestParam(value = "query") String query) {
        String tsQuery = query.trim().replace(' ', '&');
        return this.feedRepository.fullTextSearch(feed.getId(), tsQuery);
    }

    @RequestMapping(path = "{id}/random")
    public FeedItem randomItem(@PathVariable Integer id) {
        List<FeedItem> feedItems = feedItemRepository.findByFeedId(id);
        return feedItems.get(RANDOM.nextInt(feedItems.size()));
    }

    @RequestMapping(path = "{feed}/favorites")
    public List<FeedItem> favorites(@PathVariable RssFeed feed) {
        return this.feedRepository.findFavoriteItems(feed);
    }
}
