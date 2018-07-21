package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.User;
import com.fasterxml.jackson.databind.JsonNode;
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

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * @author Martin Tomasi.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTests {
    private final String password = "test";
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void registerUser() throws Exception {
        MvcResult result = TestUtil.registerUser(this.mvc, "martin2", this.password);
        assertEquals(result.getResponse().getStatus(), 200);
    }

    @Test
    public void getUserinfo() throws Exception {
        String token = TestUtil.getToken(this.mvc, "martin2", this.password);
        MvcResult result = this.mvc.perform(get("/api/users")
                .header("Authorization", "Bearer " + token))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        User user = this.objectMapper.readValue(response, User.class);
        assertEquals(user.getName(), "martin2");
        assertEquals(user.getEmail(), "martin2@gmail.com");
        assertEquals(user.getPwHash(), null);
    }


    @Test
    public void getHistoryTest() throws Exception {
        String token = TestUtil.getToken(this.mvc, "martin2", this.password);
        MvcResult result = this.mvc.perform(get("/api/users/history")
                .param("page", String.valueOf(0))
                .header("Authorization", "Bearer " + token))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(response);
        assertEquals(0, node.get("numberOfElements").asInt());
    }
}
