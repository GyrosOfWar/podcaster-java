package at.wambo.podcaster.controller;

import at.wambo.podcaster.auth.JwtResponse;
import at.wambo.podcaster.forms.CreateUserRequest;
import at.wambo.podcaster.model.LoginRequest;
import at.wambo.podcaster.model.RssFeed;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
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

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author martin
 *         Created on 25.08.16.
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ImageCacheControllerTests {
    private static final String PASSWORD = "test";
    private static final String USERNAME = "testImg";
    private static final String RSS_URL = DigestUtils.sha256Hex("http://static.giantbomb.com/uploads/original/11/110673/2894068-3836779617-28773.png");
    private static final String ITEM_URL = DigestUtils.sha256Hex("http://static.giantbomb.com/uploads/original/0/31/2878095-1216252340-theod.jpg");

    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @Before
    public void before() throws Exception {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();

        registerUser();
        addPodcast();
        this.token = getToken();
    }

    private void registerUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail(USERNAME + "@gmail.com");
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        request.setPasswordRepeated(PASSWORD);
        String json = objectMapper.writeValueAsString(request);
        MvcResult result = this.mvc.perform(post("/auth/register")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    private String getToken() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername(USERNAME);
        login.setPassword(PASSWORD);
        String json = objectMapper.writeValueAsString(login);
        MvcResult result = this.mvc.perform(post("/auth/token")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        JwtResponse response = objectMapper.readValue(content, JwtResponse.class);
        return response.getToken();
    }

    private void addPodcast() throws Exception {
        String token = getToken();
        String url = RssFeedControllerTests.FEED_URL;
        MvcResult result = this.mvc.perform(post("/api/feeds")
                .param("url", url)
                .header("Authorization", "Bearer " + token))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        RssFeed feed = this.objectMapper.readValue(response, RssFeed.class);
        int size = feed.getItems().size();
        assertEquals(size, 0);
    }
 
    @Test
    public void testFeedImage() throws Exception {
        String url = "/api/image/" + RSS_URL;
        MvcResult result = this.mvc.perform(get(url).param("size", "150").header("Authorization", "Bearer " + token)).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
        assertEquals(result.getResponse().getContentType(), "image/jpeg");
    }

    @Test
    public void testItemImage() throws Exception {
        String url = "/api/image/" + ITEM_URL;
        MvcResult result = this.mvc.perform(get(url).param("size", "350")
                .header("Authorization", "Bearer " + token)
        ).andReturn();
        assertEquals(result.getResponse().getStatus(), 200);
        assertEquals(result.getResponse().getContentType(), "image/jpeg");
    }
}
