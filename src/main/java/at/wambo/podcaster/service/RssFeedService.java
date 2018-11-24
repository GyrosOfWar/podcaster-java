package at.wambo.podcaster.service;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.RssFeed;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.repository.FeedItemRepository;
import at.wambo.podcaster.repository.RssFeedRepository;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class RssFeedService {

  private static final Random RANDOM = new Random();
  private final FeedItemRepository feedItemRepository;
  private final RssFeedRepository rssFeedRepository;
  private HttpClient httpClient;

  private static String fixUrl(String url) throws MalformedURLException {
    if (url.startsWith("http")) {
      return url;
    } else {
      return "http://" + url;
    }
  }

  @PostConstruct
  public void init() {
    httpClient = HttpClient.newBuilder()
        .build();
  }

  private Optional<FetchFeedResponse> fetchFeed(String url, String feedETag)
      throws IOException, InterruptedException, FeedException {
    String fixedUrl = fixUrl(url);
    HttpRequest request = HttpRequest.newBuilder()
        .GET()
        .uri(URI.create(fixedUrl))
        .build();
    HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
    String eTag = response.headers().firstValue("ETag").orElse(null);
    if (eTag != null && feedETag != null && eTag.equals(feedETag)) {
      return Optional.empty();
    } else {
      SyndFeedInput input = new SyndFeedInput();
      SyndFeed feed = input.build(new StringReader(response.body()));
      return Optional.of(new FetchFeedResponse(feed, eTag));
    }
  }

  public RssFeed fetchAndSaveFeed(String urlString, User user)
      throws InterruptedException, IOException, FeedException {
    FetchFeedResponse response = fetchFeed(urlString, null).get();
    SyndFeed syndFeed = response.getFeed();

    RssFeed rssFeed = new RssFeed();
    String imageUrl = syndFeed.getImage().getUrl();
    rssFeed.setFeedUrl(urlString);
    rssFeed.setImageUrl(imageUrl);
    rssFeed.setTitle(syndFeed.getTitle());
    List<FeedItem> items = syndFeed.getEntries()
        .stream()
        .map(e -> FeedItem.fromEntry(rssFeed, e, user))
        .collect(Collectors.toList());
    rssFeed.setItems(items);
    rssFeed.setOwner(user);
    rssFeed.setLastETag(response.getETag());

    return rssFeedRepository.save(rssFeed);
  }

  public List<FeedItem> refresh(RssFeed rssFeed, User user)
      throws InterruptedException, IOException, FeedException {
    var result = fetchFeed(rssFeed.getFeedUrl(), rssFeed.getLastETag());

    List<FeedItem> newItems = new ArrayList<>();

    if (result.isEmpty()) {
      return Collections.emptyList();
    } else {
      var response = result.get();
      var feed = response.getFeed();

      for (SyndEntry entry : feed.getEntries()) {
        FeedItem item = FeedItem.fromEntry(rssFeed, entry, user);
        int byGuid = feedItemRepository.findByGuid(item.getGuid()).size();
        if (byGuid == 0) {
          log.info("Found no entries with GUID {}", item.getGuid());
          int byLink = feedItemRepository.findByLink(item.getLink()).size();
          if (byLink == 0) {
            log.info("Found no entries with link {}", item.getLink());
            newItems.add(feedItemRepository.save(item));
          }
        }
      }
      rssFeed.setLastETag(response.getETag());
      rssFeedRepository.save(rssFeed);
      return newItems;
    }
  }

  public Iterable<RssFeed> findAll() {
    return rssFeedRepository.findAll();
  }

  public void delete(RssFeed feed) {
    feedItemRepository.deleteByFeedId(feed.getId());
    rssFeedRepository.delete(feed);
  }

  public Page<FeedItem> getItems(Integer feedId, Pageable pageable) {
    return feedItemRepository.findByFeedIdOrderByPubDateDesc(feedId, pageable);
  }

  public List<FeedItem> fullTextSearch(RssFeed feed, String query) {
    String tsQuery = query.trim().replace(' ', '&');
    return rssFeedRepository.fullTextSearch(feed.getId(), tsQuery);
  }

  public FeedItem getRandomItem(Integer id) {
    List<FeedItem> feedItems = feedItemRepository.findByFeedId(id);
    return feedItems.get(RANDOM.nextInt(feedItems.size()));
  }

  public List<FeedItem> getFavoriteItems(Integer feedId) {
    return rssFeedRepository.findFavoriteItems(feedId);
  }

  @Value
  private static class FetchFeedResponse {

    private SyndFeed feed;
    private String eTag;
  }
}
