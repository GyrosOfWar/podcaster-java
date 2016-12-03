package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.repository.FeedItemRepository;
import at.wambo.podcaster.requests.ChangeFeedItemRequest;
import at.wambo.podcaster.service.HistoryService;
import at.wambo.podcaster.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
/**
 * @author martin
 *         Created on 03.07.16.
 */
@RestController
@RequestMapping(path = "/api/feed_items/")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FeedItemController {
    private final @NonNull FeedItemRepository feedItemRepository;
    private final @NonNull HistoryService historyService;
    private final @NonNull ObjectMapper objectMapper;

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public FeedItem getFeedItem(@PathVariable Integer id) {
        return feedItemRepository.findOne(id);
    }

    @RequestMapping(path = "{id}", method = RequestMethod.POST, consumes = "application/json")
    public FeedItem postFeedItem(@PathVariable Integer id, @RequestBody String data) throws IOException {
        FeedItem existing = this.feedItemRepository.findOne(id);
        if (existing == null) {
            throw new IllegalArgumentException("Item does not exist");
        }
        ChangeFeedItemRequest request = objectMapper.readValue(data, ChangeFeedItemRequest.class);

        existing.setFavorite(request.isFavorite());
        existing.setLastPosition(request.getLastPosition());

        FeedItem item = this.feedItemRepository.save(existing);
        this.historyService.addToHistory(Util.getUser(), item);
        return item;
    }

}
