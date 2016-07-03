package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.RssFeed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.Assert.*;

/**
 * @author martin
 *         Created on 03.07.16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RssFeedControllerTests {
    private final String feedUrl = "http://www.giantbomb.com/podcast-xml/beastcast";

    @Autowired
    private TestRestTemplate restTemplate;

    private boolean hasFeeds() {
        HttpEntity<RssFeed[]> feeds = this.restTemplate.getForEntity(
                "/api/feeds",
                RssFeed[].class,
                (String) null);
        RssFeed[] feedArray = feeds.getBody();
        return feedArray.length > 0;
    }

    private int getFeedId() {
        int id = 1;
        if (!hasFeeds()) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("url", this.feedUrl);
            RssFeed feed = this.restTemplate.postForEntity("/api/feeds", map, RssFeed.class).getBody();
            id = feed.getId();
        }
        return id;
    }

    @Test
    public void addFeed() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("url", this.feedUrl);
        HttpEntity<RssFeed> feed = this.restTemplate.postForEntity("/api/feeds", map, RssFeed.class);
        int size = feed.getBody().getItems().size();
        assertEquals(size, 0);
    }

    @Test
    public void feedWithId() {
        int id = getFeedId();
        HttpEntity<RssFeed> feed = this.restTemplate.getForEntity("/api/feeds/{id}", RssFeed.class, id);
        assertEquals(feed.getBody().getItems().size(), 0);
        assertEquals(feed.getBody().getTitle(), "The Giant Beastcast");
    }

    @Test
    public void postFeed() {

    }

    @Test
    public void getPaginated() {
        int id = getFeedId();
        HttpEntity<FeedItem[]> entity = this.restTemplate.getForEntity("/api/feeds/{id}/0/15", FeedItem[].class, id);
        FeedItem[] items = entity.getBody();
        assertEquals(items.length, 15);
    }

    @Test
    public void fullTextSearch() {
        int id = getFeedId();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("query", "dota");
        HttpEntity<FeedItem[]> entity = this.restTemplate.postForEntity("/api/feeds/{id}/search", map, FeedItem[].class, id);
        FeedItem[] result = entity.getBody();
        for (FeedItem item : result) {
            String t = item.getTitle() + " " + item.getDescription();
            assertTrue(t.contains("dota"));
        }
    }

    @Test
    public void randomItem() {
        int id = getFeedId();
        HttpEntity<FeedItem> entity = this.restTemplate.getForEntity("/api/feeds/{id}/random", FeedItem.class, id);
        FeedItem item = entity.getBody();
        assertNotNull(item);
    }

    @Test
    public void favorites() {
        ObjectMapper mapper = new ObjectMapper();

        int id = getFeedId();
        HttpEntity<FeedItem[]> entity = this.restTemplate.getForEntity("/api/feeds/{id}/0/15", FeedItem[].class, id);
        FeedItem[] items = entity.getBody();
        for (int i = 0; i < 5; i++) {
            FeedItem item = items[i];
            item.setFavorite(true);
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            String json = null;
            try {
                json = mapper.writeValueAsString(item);
            } catch (JsonProcessingException e) {
                fail(e.toString());
            }
            map.add("item", json);
            HttpEntity<String> result = this.restTemplate.postForEntity("/api/feed_items/{id}", map, String.class, item.getId());
            System.out.println(result);
        }

//        HttpEntity<FeedItem[]> favorites = this.restTemplate.getForEntity("/api/feeds/{id}/favorites", FeedItem[].class, id);
//
//        for (FeedItem item : favorites.getBody()) {
//            assertTrue(item.isFavorite());
//        }
    }
}
