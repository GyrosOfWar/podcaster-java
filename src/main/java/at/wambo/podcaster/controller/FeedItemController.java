package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.repository.FeedItemRepository;
import at.wambo.podcaster.service.HistoryService;
import at.wambo.podcaster.util.Util;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;

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

    @RequestMapping(path = "{id}", method = RequestMethod.POST)
    public String postFeedItem(@PathVariable Integer id, @RequestParam(value = "data") String json) throws IOException {
        FeedItem existing = this.feedItemRepository.findOne(id);
        if (existing == null) {
            return "Item doesn't exist";
        }
        JsonNode data = objectMapper.readTree(json);
        existing.setFavorite(data.get("favorite").booleanValue());
        double lastPosition = data.get("lastPosition").asDouble();
        existing.setLastPosition(Duration.ofSeconds((long) lastPosition));

        FeedItem item = this.feedItemRepository.save(existing);
        this.historyService.addToHistory(Util.getUser(), item);
        return "OK";
    }

}
