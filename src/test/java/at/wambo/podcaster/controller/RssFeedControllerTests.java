package at.wambo.podcaster.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.HistoryEntry;
import at.wambo.podcaster.model.RssFeed;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.requests.ChangeFeedItemRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author martin Created on 03.07.16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RssFeedControllerTests {

  final static String FEED_URL = "https://www.giantbomb.com/podcast-xml/beastcast";
  private static final String password = "test";
  @Autowired
  private WebApplicationContext context;
  @Autowired
  private ObjectMapper objectMapper;
  private User user = new User("martin", "martin@gmail.com",
      UserController.PASSWORD_ENCODER.encode(password));
  private MockMvc mvc;
  private boolean userRegistered = false;
  private String token;

  @Before
  public void setup() {
    this.mvc = MockMvcBuilders
        .webAppContextSetup(this.context)
        .apply(springSecurity())
        .build();
  }

  private int hasFeeds() throws Exception {
    MvcResult result = this.mvc.perform(get("/api/feeds")
        .header("Authorization", "Bearer " + token))
        .andReturn();

    assertEquals(200, result.getResponse().getStatus());
    String response = result.getResponse().getContentAsString();
    RssFeed[] feeds = this.objectMapper.readValue(response, RssFeed[].class);
    return feeds.length > 0 ? feeds[0].getId() : -1;
  }


  private void registerUser() throws Exception {
    if (!this.userRegistered) {
      TestUtil.registerUser(this.mvc, this.user.getUsername(), password);
      this.userRegistered = true;
      this.token = TestUtil.getToken(this.mvc, this.user.getUsername(), password);
    }
  }

  private int getFeedId() throws Exception {
    registerUser();
    int feedId = hasFeeds();
    if (feedId == -1) {
      MvcResult result = this.mvc.perform(post("/api/feeds")
          .param("url", FEED_URL)
          .header("Authorization", "Bearer " + token))
          .andReturn();
      assertEquals(200, result.getResponse().getStatus());
      String response = result.getResponse().getContentAsString();
      RssFeed feed = this.objectMapper.readValue(response, RssFeed.class);
      return feed.getId();
    } else {
      return feedId;
    }
  }

  @Ignore
  public void addFeed() throws Exception {
    registerUser();
    MvcResult result = this.mvc.perform(post("/api/feeds")
        .param("url", FEED_URL)
        .header("Authorization", "Bearer " + token))
        .andReturn();
    String response = result.getResponse().getContentAsString();
    RssFeed feed = this.objectMapper.readValue(response, RssFeed.class);
    assertEquals(null, feed.getItems());
  }

  @Test
  public void feedWithId() throws Exception {
    int id = getFeedId();
    MvcResult result = this.mvc.perform(get("/api/feeds/" + id)
        .header("Authorization", "Bearer " + token))
        .andReturn();
    String response = result.getResponse().getContentAsString();
    RssFeed feed = this.objectMapper.readValue(response, RssFeed.class);
    assertEquals(feed.getItems(), null);
    assertEquals(feed.getTitle(), "The Giant Beastcast");
  }

  //
  @Test
  public void getPaginated() throws Exception {
    int id = getFeedId();
    String url = String.format("/api/feeds/%s/items", id);
    MvcResult result = this.mvc.perform(get(url)
        .header("Authorization", "Bearer " + token)
        .param("page", "0")
        .param("size", "15"))
        .andReturn();
    String response = result.getResponse().getContentAsString();
    List<FeedItem> page = TestUtil.deserializePage(response, FeedItem.class);
    assertEquals(15, page.size());
  }

  @Test
  public void fullTextSearch() throws Exception {
    int id = getFeedId();
    String url = String.format("/api/feeds/%s/search", id);
    MvcResult result = this.mvc.perform(get(url)
        .param("query", "dota")
        .header("Authorization", "Bearer " + token))
        .andReturn();
    String response = result.getResponse().getContentAsString();
    FeedItem[] items = this.objectMapper.readValue(response, FeedItem[].class);
    for (FeedItem item : items) {
      String t = item.getTitle() + " " + item.getDescription();
      assertTrue(String.format("%s did not contain 'dota'", t), t.toLowerCase().contains("dota"));
    }
  }

  @Test
  public void randomItem() throws Exception {
    int id = getFeedId();
    String url = String.format("/api/feeds/%s/random", id);
    MvcResult result = this.mvc.perform(get(url)
        .header("Authorization", "Bearer " + token))
        .andReturn();
    String response = result.getResponse().getContentAsString();
    FeedItem item = this.objectMapper.readValue(response, FeedItem.class);

    assertNotNull(item);
  }

  @Test
  public void favorites() throws Exception {
    int id = getFeedId();
    String url = String.format("/api/feeds/%s/items", id);
    MvcResult result = this.mvc.perform(get(url)
        .header("Authorization", "Bearer " + token))
        .andReturn();
    String response = result.getResponse().getContentAsString();
    List<FeedItem> items = TestUtil.deserializePage(response, FeedItem.class);

    for (int i = 0; i < 5; i++) {
      FeedItem item = items.get(i);
      String itemUrl = String.format("/api/feed_items/%s", item.getId());
      ChangeFeedItemRequest request = new ChangeFeedItemRequest();
      request.setFavorite(true);
      request.setLastPosition(Duration.ofSeconds(10));
      MvcResult res = this.mvc.perform(post(itemUrl)
          .content(objectMapper.writeValueAsString(request))
          .contentType(MediaType.APPLICATION_JSON)
          .header("Authorization", "Bearer " + token))
          .andReturn();
      if (res.getResolvedException() != null) {
        throw res.getResolvedException();
      }
      String resp = res.getResponse().getContentAsString();
      assertTrue(resp.length() > 0);
    }
    String favUrl = String.format("/api/feeds/%s/favorites", id);
    result = this.mvc.perform(get(favUrl)
        .header("Authorization", "Bearer " + token))
        .andReturn();
    response = result.getResponse().getContentAsString();
    FeedItem[] favorites = this.objectMapper.readValue(response, FeedItem[].class);

    for (FeedItem item : favorites) {
      assertTrue(item.isFavorite());
    }

    result = this.mvc.perform(get("/api/users/history")
        .header("Authorization", "Bearer " + token))
        .andReturn();
    response = result.getResponse().getContentAsString();
    List<HistoryEntry> history = TestUtil.deserializePage(response, HistoryEntry.class);
    assertEquals(5, history.size());
  }
}
