package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.Bookmark;
import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.repository.BookmarkRepository;
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
import java.time.Duration;
import java.util.Optional;

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
    private final @NonNull BookmarkRepository bookmarkRepository;

    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public Optional<FeedItem> getFeedItem(@PathVariable Integer id) {
        return feedItemRepository.findById(id);
    }

    @RequestMapping(path = "{id}", method = RequestMethod.POST, consumes = "application/json")
    public FeedItem postFeedItem(@PathVariable Integer id, @RequestBody String data) throws IOException {
        Optional<FeedItem> existing = this.feedItemRepository.findById(id);
        if (!existing.isPresent()) {
            throw new IllegalArgumentException("Item does not exist");
        }
        FeedItem item = existing.get();
        ChangeFeedItemRequest request = objectMapper.readValue(data, ChangeFeedItemRequest.class);

        item.setFavorite(request.isFavorite());
        item.setLastPosition(request.getLastPosition());

        FeedItem saved = this.feedItemRepository.save(item);
        this.historyService.addToHistory(Util.getUser(), saved);
        return saved;
    }

    @RequestMapping(path = "{item}/bookmark", method = RequestMethod.POST)
    public Bookmark addBookmark(@PathVariable FeedItem item, @RequestParam("position") String positionString) {
        User user = Util.getUser();
        Duration position = Duration.parse(positionString);
        Bookmark bookmark = new Bookmark(null, position, user, item);
        Bookmark saved = bookmarkRepository.save(bookmark);
        item.getBookmarks().add(saved);
        feedItemRepository.save(item);
        return saved;
    }
}
