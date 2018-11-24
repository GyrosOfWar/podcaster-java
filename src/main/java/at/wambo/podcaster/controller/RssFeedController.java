package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.RssFeed;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.service.HistoryService;
import at.wambo.podcaster.service.RssFeedService;
import com.rometools.rome.io.FeedException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

  private final RssFeedService rssFeedService;
  private final HistoryService historyService;

  @RequestMapping(method = RequestMethod.GET)
  public Iterable<RssFeed> getFeeds() {
    return this.rssFeedService.findAll();
  }

  @RequestMapping(method = RequestMethod.POST)
  public RssFeed addFeed(@RequestParam(value = "url") String url,
      @AuthenticationPrincipal User user) throws InterruptedException, IOException, FeedException {
    return rssFeedService.fetchAndSaveFeed(url, user);
  }

  // TODO add JSON view
  @RequestMapping(path = "{feed}", method = RequestMethod.GET)
  public RssFeed feedWithId(@PathVariable RssFeed feed) {
    feed.setItems(Collections.emptyList());
    return feed;
  }

  @Transactional
  @RequestMapping(path = "{feed}", method = RequestMethod.DELETE)
  public ResponseEntity<String> deleteFeed(@PathVariable RssFeed feed) {
    historyService.deleteForFeed(feed.getId());
    rssFeedService.delete(feed);
    return ResponseEntity.ok("OK");
  }

  @RequestMapping(path = "{feed}", method = RequestMethod.POST)
  public List<FeedItem> refresh(@PathVariable RssFeed feed, @AuthenticationPrincipal User user)
      throws InterruptedException, IOException, FeedException {
    return rssFeedService.refresh(feed, user);
  }

  @RequestMapping(path = "{feedId}/items")
  public Page<FeedItem> getPaginated(@PathVariable Integer feedId, Pageable pageable) {
    return rssFeedService.getItems(feedId, pageable);
  }

  @RequestMapping(path = "{feed}/search", method = RequestMethod.GET)
  public List<FeedItem> searchItems(@PathVariable RssFeed feed,
      @RequestParam(value = "query") String query) {
    return rssFeedService.fullTextSearch(feed, query);
  }

  @RequestMapping(path = "{id}/random")
  public FeedItem randomItem(@PathVariable Integer id) {
    return rssFeedService.getRandomItem(id);
  }

  @RequestMapping(path = "{feedId}/favorites")
  public List<FeedItem> favorites(@PathVariable Integer feedId) {
    return rssFeedService.getFavoriteItems(feedId);
  }
}
