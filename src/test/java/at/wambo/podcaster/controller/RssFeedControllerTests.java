package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.RssFeed;
import at.wambo.podcaster.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author martin
 *         Created on 03.07.16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RssFeedControllerTests {
    private static final String password = "test";
    private final String feedUrl = "http://www.giantbomb.com/podcast-xml/beastcast";
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    private User user = new User(-1, "martin", "martin.tomasi@gmail.com", UserController.PASSWORD_ENCODER.encode(password));
    private MockMvc mvc;
    private boolean userRegistered = false;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    private int hasFeeds() throws Exception {
        MvcResult result = mvc.perform(get("/api/feeds")
                .with(httpBasic(user.getName(), password))).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String response = result.getResponse().getContentAsString();
        RssFeed[] feeds = objectMapper.readValue(response, RssFeed[].class);
        return feeds.length > 0 ? feeds[0].getId() : -1;
    }


    private void registerUser() throws Exception {
        if (!userRegistered) {
            MvcResult result = mvc.perform(post("/register")
                    .param("email", user.getEmail())
                    .param("username", user.getName())
                    .param("password", password)
                    .param("passwordRepeated", password).with(csrf()))
                    .andReturn();
            userRegistered = true;
        }
    }

    private int getFeedId() throws Exception {
        registerUser();
        int feedId = hasFeeds();
        if (feedId == -1) {
            MvcResult result = mvc.perform(post("/api/feeds")
                    .param("url", this.feedUrl)
                    .with(httpBasic(user.getName(), password))).andReturn();
            assertEquals(200, result.getResponse().getStatus());
            String response = result.getResponse().getContentAsString();
            RssFeed feed = objectMapper.readValue(response, RssFeed.class);
            return feed.getId();
        } else {
            return feedId;
        }
    }

    @Test
    public void addFeed() throws Exception {
        registerUser();
        MvcResult result = mvc.perform(post("/api/feeds")
                .param("url", this.feedUrl)
                .with(httpBasic(user.getName(), password))).andReturn();
        String response = result.getResponse().getContentAsString();
        RssFeed feed = objectMapper.readValue(response, RssFeed.class);
        int size = feed.getItems().size();
        assertEquals(size, 0);
    }

    @Test
    public void feedWithId() throws Exception {
        int id = getFeedId();
        MvcResult result = mvc.perform(get("/api/feeds/" + id)
                .with(httpBasic(user.getName(), password))).andReturn();
        String response = result.getResponse().getContentAsString();
        RssFeed feed = objectMapper.readValue(response, RssFeed.class);
        assertEquals(feed.getItems().size(), 0);
        assertEquals(feed.getTitle(), "The Giant Beastcast");
    }

    //
    @Test
    public void getPaginated() throws Exception {
        int id = getFeedId();
        String url = String.format("/api/feeds/%s/0/15", id);
        MvcResult result = mvc.perform(get(url)
                .with(httpBasic(user.getName(), password))).andReturn();
        String response = result.getResponse().getContentAsString();
        FeedItem[] items = objectMapper.readValue(response, FeedItem[].class);
        assertEquals(items.length, 15);
    }

    @Test
    public void fullTextSearch() throws Exception {
        int id = getFeedId();
        String url = String.format("/api/feeds/%s/search", id);
        MvcResult result = mvc.perform(get(url)
                .param("query", "dota")
                .with(httpBasic(user.getName(), password)))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        FeedItem[] items = objectMapper.readValue(response, FeedItem[].class);
        for (FeedItem item : items) {
            String t = item.getTitle() + " " + item.getDescription();
            assertTrue(t.contains("dota"));
        }
    }

    @Test
    public void randomItem() throws Exception {
        int id = getFeedId();
        String url = String.format("/api/feeds/%s/random", id);
        MvcResult result = mvc.perform(get(url).with(httpBasic(user.getName(), password))).andReturn();
        String response = result.getResponse().getContentAsString();
        FeedItem item = objectMapper.readValue(response, FeedItem.class);

        assertNotNull(item);
    }

    @Test
    public void favorites() throws Exception {
        int id = getFeedId();
        String url = String.format("/api/feeds/%s/0/15", id);
        MvcResult result = mvc.perform(get(url).with(httpBasic(user.getName(), password))).andReturn();
        String response = result.getResponse().getContentAsString();
        FeedItem[] items = objectMapper.readValue(response, FeedItem[].class);

        for (int i = 0; i < 5; i++) {
            FeedItem item = items[i];
            item.setFavorite(true);
            String itemUrl = String.format("/api/feed_items/%s", item.getId());
            mvc.perform(post(itemUrl)
                    .param("item", objectMapper.writeValueAsString(item))
                    .with(httpBasic(user.getName(), password))).andReturn();
        }
        String favUrl = String.format("/api/feeds/%s/favorites", id);
        result = mvc.perform(get(favUrl).with(httpBasic(user.getName(), password))).andReturn();
        response = result.getResponse().getContentAsString();
        FeedItem[] favorites = objectMapper.readValue(response, FeedItem[].class);

        for (FeedItem item : favorites) {
            assertTrue(item.isFavorite());

        }
    }
}
