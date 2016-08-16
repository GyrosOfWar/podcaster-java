package at.wambo.podcaster.controller;

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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void registerUser() throws Exception {
        MvcResult result = mvc.perform(post("/register")
                .param("email", "test123@gmail.com")
                .param("username", "martin2")
                .param("password", password)
                .param("passwordRepeated", password).with(csrf()))
                .andReturn();

        assertEquals(result.getResponse().getStatus(), 302);
    }

    @Test
    public void registerUserWithoutCsrf() throws Exception {
        MvcResult result = mvc.perform(post("/register")
                .param("email", "test2345@gmail.com")
                .param("username", "martin3")
                .param("password", password)
                .param("passwordRepeated", password).with(csrf()))
                .andReturn();
        assertEquals(302, result.getResponse().getStatus());
    }
}
