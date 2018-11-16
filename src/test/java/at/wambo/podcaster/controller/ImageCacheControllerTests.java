package at.wambo.podcaster.controller;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.apache.commons.codec.digest.DigestUtils;
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
  private static final String RSS_URL = DigestUtils.sha256Hex(
      "http://static.giantbomb.com/uploads/original/11/110673/2894068-3836779617-28773.png");
  private static final String ITEM_URL = DigestUtils
      .sha256Hex("http://static.giantbomb.com/uploads/original/0/31/2878095-1216252340-theod.jpg");

  private MockMvc mvc;
  @Autowired
  private WebApplicationContext context;

  private String token;

  @Before
  public void before() throws Exception {
    this.mvc = MockMvcBuilders
        .webAppContextSetup(this.context)
        .apply(springSecurity())
        .build();
    TestUtil.registerUser(this.mvc, USERNAME, PASSWORD);
    this.token = TestUtil.getToken(this.mvc, USERNAME, PASSWORD);
    TestUtil.addPodcast(this.mvc, this.token, RssFeedControllerTests.FEED_URL);
  }

  @Test
  public void testFeedImage() throws Exception {
    String url = "/api/images/" + RSS_URL;
    MvcResult result = this.mvc.perform(get(url)
        .param("size", "150")
        .header("Authorization", "Bearer " + token))
        .andReturn();
    assertEquals(200, result.getResponse().getStatus());
    assertEquals("image/jpeg", result.getResponse().getContentType());
  }

  @Test
  public void testItemImage() throws Exception {
    String url = "/api/images/" + ITEM_URL;
    MvcResult result = this.mvc.perform(get(url)
        .param("size", "350")
        .header("Authorization", "Bearer " + token))
        .andReturn();
    assertEquals(200, result.getResponse().getStatus());
    assertEquals("image/jpeg", result.getResponse().getContentType());
  }
}
