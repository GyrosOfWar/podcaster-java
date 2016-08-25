package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.repository.FeedItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * @author martin
 *         Created on 03.07.16.
 */
@RestController
@RequestMapping(path = "/api/feed_items")
public class FeedItemController {
    private final FeedItemRepository feedItemRepository;

    @Autowired
    public FeedItemController(FeedItemRepository feedItemRepository) {
        Assert.notNull(feedItemRepository);
        this.feedItemRepository = feedItemRepository;
    }

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public FeedItem getFeedItem(@PathVariable FeedItem item) {
        return item;
    }

    @RequestMapping(path = "{id}", method = RequestMethod.POST)
    public String postFeedItem(@PathVariable Integer id, @RequestParam(value = "item") FeedItem newItem) {
        if (newItem == null) {
            return "Missing data";
        }

        FeedItem existing = this.feedItemRepository.findOne(id);
        if (existing == null) {
            return "Item doesn't exist";
        }
        existing.setFavorite(newItem.isFavorite());
        existing.setLastPosition(newItem.getLastPosition());

        this.feedItemRepository.save(existing);
        return "OK";
    }

}
