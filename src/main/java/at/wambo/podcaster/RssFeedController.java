package at.wambo.podcaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin
 *         01.07.2016
 */
@RestController
public class RssFeedController {
    @Autowired
    private RssFeedRepository feedRepository;
    @Autowired
    private FeedItemRepository feedItemRepository;

    private static final String JSON = "application/json; charset=UTF-8";

    @RequestMapping(path = "/api/feeds", method = RequestMethod.GET, produces = JSON)
    public List<RssFeed> getFeeds() {
        List<RssFeed> feeds = new ArrayList<>();
        feedRepository.findAll().forEach(feeds::add);
        return feeds;
    }

    @RequestMapping(path = "/api/feeds", method = RequestMethod.POST, produces = JSON)
    public RssFeed addFeed(@RequestBody String url) {
        RssFeed feed = RssFeed.fromUrl(url);
        feed = feedRepository.save(feed);
        return feed;
    }

    @RequestMapping(path = "/api/feeds/{id}", method = RequestMethod.GET, produces = JSON)
    public RssFeed feedWithId(@PathVariable Integer id) {
        return feedRepository.findOne(id);
    }

    @RequestMapping(path = "/api/feeds/{id}", method = RequestMethod.POST, produces = JSON)
    public List<FeedItem> refresh(@PathVariable Integer id) {
        RssFeed feed = feedRepository.findOne(id);
        if (feed != null) {
            return feed.refresh(feedItemRepository);
        } else {
            return null;
        }
    }
}
