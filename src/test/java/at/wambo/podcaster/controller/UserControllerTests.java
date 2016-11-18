package at.wambo.podcaster.controller;

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

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * @author Martin Tomasi.
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
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
//        CreateUserRequest request = new CreateUserRequest();
//        request.setEmail("test123@gmail.com");
//        request.setUsername("martin2");
//        request.setPassword(this.password);
//        request.setPasswordRepeated(this.password);
//        String json = objectMapper.writeValueAsString(request);
//        this.mvc.perform(post("/auth/register")
//                .content(json))
//                .andReturn();
//
//
        MvcResult result = this.mvc.perform(post("/register")
                .param("email", "test123@gmail.com")
                .param("username", "martin2")
                .param("password", this.password)
                .param("passwordRepeated", this.password))
                .andReturn();

        assertEquals(result.getResponse().getStatus(), 302);
    }

    @Test
    public void registerUserWithoutCsrf() throws Exception {
        MvcResult result = this.mvc.perform(post("/register")
                .param("email", "test2345@gmail.com")
                .param("username", "martin3")
                .param("password", this.password)
                .param("passwordRepeated", this.password))
                .andReturn();
        assertEquals(302, result.getResponse().getStatus());
    }

    @Test
    public void getUserinfo() throws Exception {
        MvcResult result = this.mvc.perform(get("/api/user")
                .with(httpBasic("martin2", this.password))).andReturn();
        String response = result.getResponse().getContentAsString();
        User user = this.objectMapper.readValue(response, User.class);
        assertEquals(user.getName(), "martin2");
        assertEquals(user.getEmail(), "test123@gmail.com");
        assertEquals(user.getPwHash(), null);
    }
}
