package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.RssFeed;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.repository.FeedItemRepository;
import at.wambo.podcaster.repository.RssFeedRepository;
import at.wambo.podcaster.service.HistoryService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Martin 01.07.2016
 */
@RestController
@RequestMapping(path = "/api/feeds")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RssFeedController {

  private static final Random RANDOM = new Random();

  private final @NonNull
  RssFeedRepository feedRepository;
  private final @NonNull
  FeedItemRepository feedItemRepository;
  private final @NonNull
  HistoryService historyService;

  @RequestMapping(method = RequestMethod.GET)
  public Iterable<RssFeed> getFeeds() {
    return this.feedRepository.findAll();
  }

  @RequestMapping(method = RequestMethod.POST)
  public RssFeed addFeed(@RequestParam(value = "url") String url,
      @AuthenticationPrincipal User user) {
    RssFeed feed = RssFeed.fromUrl(url, user);
    return this.feedRepository.save(feed);
  }

  @RequestMapping(path = "{feed}", method = RequestMethod.GET)
  public RssFeed feedWithId(@PathVariable RssFeed feed) {
    feed.setItems(Collections.emptyList());
    return feed;
  }

  @Transactional
  @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
  public String deleteFeed(@PathVariable Integer id) {
    Optional<RssFeed> result = feedRepository.findById(id);
    if (result.isPresent()) {
      historyService.deleteForFeed(id);
      feedItemRepository.deleteByFeedId(id);
      feedRepository.deleteById(id);
      return "OK";
    } else {
      return "Feed doesn't exist";
    }
  }

  @RequestMapping(path = "{feed}", method = RequestMethod.POST)
  public List<FeedItem> refresh(@PathVariable RssFeed feed, @AuthenticationPrincipal User user) {
    return feed.refresh(this.feedItemRepository, user);
  }

  @RequestMapping(path = "{feedId}/items")
  public Page<FeedItem> getPaginated(@PathVariable Integer feedId, Pageable pageable) {
    return this.feedItemRepository.findByFeedIdOrderByPubDateDesc(feedId, pageable);
  }

  @RequestMapping(path = "{feed}/search", method = RequestMethod.GET)
  public List<FeedItem> searchItems(@PathVariable RssFeed feed,
      @RequestParam(value = "query") String query) {
    String tsQuery = query.trim().replace(' ', '&');
    return this.feedRepository.fullTextSearch(feed.getId(), tsQuery);
  }

  @RequestMapping(path = "{id}/random")
  public FeedItem randomItem(@PathVariable Integer id) {
    List<FeedItem> feedItems = feedItemRepository.findByFeedId(id);
    return feedItems.get(RANDOM.nextInt(feedItems.size()));
  }

  @RequestMapping(path = "{feedId}/favorites")
  public List<FeedItem> favorites(@PathVariable Integer feedId) {
    return this.feedRepository.findFavoriteItems(feedId);
  }
}
