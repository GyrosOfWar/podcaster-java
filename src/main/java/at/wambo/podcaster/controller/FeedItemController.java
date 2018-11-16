package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.Bookmark;
import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.repository.BookmarkRepository;
import at.wambo.podcaster.repository.FeedItemRepository;
import at.wambo.podcaster.requests.ChangeFeedItemRequest;
import at.wambo.podcaster.service.HistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author martin Created on 03.07.16.
 */
@RestController
@RequestMapping(path = "/api/feed_items/")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FeedItemController {

  private final @NonNull
  FeedItemRepository feedItemRepository;
  private final @NonNull
  HistoryService historyService;
  private final @NonNull
  ObjectMapper objectMapper;
  private final @NonNull
  BookmarkRepository bookmarkRepository;

  @RequestMapping(path = "{id}", method = RequestMethod.GET)
  public Optional<FeedItem> getFeedItem(@PathVariable Integer id) {
    return feedItemRepository.findById(id);
  }

  @RequestMapping(path = "{id}", method = RequestMethod.POST, consumes = "application/json")
  public FeedItem postFeedItem(@PathVariable Integer id, @RequestBody String data,
      @AuthenticationPrincipal User user) throws IOException {
    Optional<FeedItem> existing = this.feedItemRepository.findById(id);
    if (!existing.isPresent()) {
      throw new IllegalArgumentException("Item does not exist");
    }
    FeedItem item = existing.get();
    ChangeFeedItemRequest request = objectMapper.readValue(data, ChangeFeedItemRequest.class);

    item.setFavorite(request.isFavorite());
    item.setLastPosition(request.getLastPosition());

    FeedItem saved = this.feedItemRepository.save(item);
    this.historyService.addToHistory(user, saved);
    return saved;
  }

  @RequestMapping(path = "{item}/bookmark", method = RequestMethod.POST)
  public Bookmark addBookmark(@PathVariable FeedItem item,
      @RequestParam("position") String positionString, @AuthenticationPrincipal User user) {
    Duration position = Duration.parse(positionString);
    Bookmark bookmark = new Bookmark(null, position, user, item);
    Bookmark saved = bookmarkRepository.save(bookmark);
    item.getBookmarks().add(saved);
    feedItemRepository.save(item);
    return saved;
  }
}
