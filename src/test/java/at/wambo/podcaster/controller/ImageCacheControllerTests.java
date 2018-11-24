package at.wambo.podcaster.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import at.wambo.podcaster.model.RssFeed;
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

/**
 * @author martin Created on 25.08.16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ImageCacheControllerTests {

  private static final String PASSWORD = "test";
  private static final String USERNAME = "testImg";

  private MockMvc mvc;
  @Autowired
  private WebApplicationContext context;

  private String token;
  private int feedId;
  private int feedItemId;

  @Before
  public void before() throws Exception {
    this.mvc = MockMvcBuilders
        .webAppContextSetup(this.context)
        .apply(springSecurity())
        .build();
    TestUtil.registerUser(this.mvc, USERNAME, PASSWORD);
    this.token = TestUtil.getToken(this.mvc, USERNAME, PASSWORD);
    RssFeed feed = TestUtil.addPodcast(this.mvc, this.token, RssFeedControllerTests.FEED_URL);
    this.feedId = feed.getId();
    this.feedItemId = feed.getItems().get(0).getId();
  }

  @Test
  public void testFeedImage() throws Exception {
    String url = "/api/images/feed/" + feedId;
    MvcResult result = this.mvc.perform(get(url)
        .param("size", "150")
        .header("Authorization", "Bearer " + token))
        .andReturn();
    assertEquals(200, result.getResponse().getStatus());
    assertEquals("image/jpeg", result.getResponse().getContentType());
  }

  @Test
  public void testItemImage() throws Exception {
    String url = "/api/images/feed_items/" + feedItemId;
    MvcResult result = this.mvc.perform(get(url)
        .param("size", "350")
        .header("Authorization", "Bearer " + token))
        .andReturn();
    assertEquals(200, result.getResponse().getStatus());
    assertEquals("image/jpeg", result.getResponse().getContentType());
  }
}
