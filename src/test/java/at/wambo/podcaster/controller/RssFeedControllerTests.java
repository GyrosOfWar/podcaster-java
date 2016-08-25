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
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RssFeedControllerTests {
    final static String FEED_URL = "http://www.giantbomb.com/podcast-xml/beastcast";
    private static final String password = "test";
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;
    private User user = new User("martin", "martin.tomasi@gmail.com", UserController.PASSWORD_ENCODER.encode(password));
    private MockMvc mvc;
    private boolean userRegistered = false;

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    private int hasFeeds() throws Exception {
        MvcResult result = this.mvc.perform(get("/api/feeds")
                .with(httpBasic(this.user.getName(), password))).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        String response = result.getResponse().getContentAsString();
        RssFeed[] feeds = this.objectMapper.readValue(response, RssFeed[].class);
        return feeds.length > 0 ? feeds[0].getId() : -1;
    }


    private void registerUser() throws Exception {
        if (!this.userRegistered) {
            MvcResult result = this.mvc.perform(post("/register")
                    .param("email", this.user.getEmail())
                    .param("username", this.user.getName())
                    .param("password", password)
                    .param("passwordRepeated", password).with(csrf()))
                    .andReturn();
            this.userRegistered = true;
        }
    }

    private int getFeedId() throws Exception {
        registerUser();
        int feedId = hasFeeds();
        if (feedId == -1) {
            MvcResult result = this.mvc.perform(post("/api/feeds")
                    .param("url", FEED_URL)
                    .with(httpBasic(this.user.getName(), password))).andReturn();
            assertEquals(200, result.getResponse().getStatus());
            String response = result.getResponse().getContentAsString();
            RssFeed feed = this.objectMapper.readValue(response, RssFeed.class);
            return feed.getId();
        } else {
            return feedId;
        }
    }

    @Test
    public void addFeed() throws Exception {
        registerUser();
        MvcResult result = this.mvc.perform(post("/api/feeds")
                .param("url", FEED_URL)
                .with(httpBasic(this.user.getName(), password))).andReturn();
        String response = result.getResponse().getContentAsString();
        RssFeed feed = this.objectMapper.readValue(response, RssFeed.class);
        int size = feed.getItems().size();
        assertEquals(size, 0);
    }

    @Test
    public void feedWithId() throws Exception {
        int id = getFeedId();
        MvcResult result = this.mvc.perform(get("/api/feeds/" + id)
                .with(httpBasic(this.user.getName(), password))).andReturn();
        String response = result.getResponse().getContentAsString();
        RssFeed feed = this.objectMapper.readValue(response, RssFeed.class);
        assertEquals(feed.getItems().size(), 0);
        assertEquals(feed.getTitle(), "The Giant Beastcast");
    }

    //
    @Test
    public void getPaginated() throws Exception {
        int id = getFeedId();
        String url = String.format("/api/feeds/%s/0/15", id);
        MvcResult result = this.mvc.perform(get(url)
                .with(httpBasic(this.user.getName(), password))).andReturn();
        String response = result.getResponse().getContentAsString();
        FeedItem[] items = this.objectMapper.readValue(response, FeedItem[].class);
        assertEquals(items.length, 15);
    }

    @Test
    public void fullTextSearch() throws Exception {
        int id = getFeedId();
        String url = String.format("/api/feeds/%s/search", id);
        MvcResult result = this.mvc.perform(get(url)
                .param("query", "dota")
                .with(httpBasic(this.user.getName(), password)))
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
        MvcResult result = this.mvc.perform(get(url).with(httpBasic(this.user.getName(), password))).andReturn();
        String response = result.getResponse().getContentAsString();
        FeedItem item = this.objectMapper.readValue(response, FeedItem.class);

        assertNotNull(item);
    }

    @Test
    public void favorites() throws Exception {
        int id = getFeedId();
        String url = String.format("/api/feeds/%s/0/15", id);
        MvcResult result = this.mvc.perform(get(url).with(httpBasic(this.user.getName(), password))).andReturn();
        String response = result.getResponse().getContentAsString();
        FeedItem[] items = this.objectMapper.readValue(response, FeedItem[].class);

        for (int i = 0; i < 5; i++) {
            FeedItem item = items[i];
            item.setFavorite(true);
            String itemUrl = String.format("/api/feed_items/%s", item.getId());
            this.mvc.perform(post(itemUrl)
                    .param("item", this.objectMapper.writeValueAsString(item))
                    .with(httpBasic(this.user.getName(), password))).andReturn();
        }
        String favUrl = String.format("/api/feeds/%s/favorites", id);
        result = this.mvc.perform(get(favUrl).with(httpBasic(this.user.getName(), password))).andReturn();
        response = result.getResponse().getContentAsString();
        FeedItem[] favorites = this.objectMapper.readValue(response, FeedItem[].class);

        for (FeedItem item : favorites) {
            assertTrue(item.isFavorite());

        }
    }
}
