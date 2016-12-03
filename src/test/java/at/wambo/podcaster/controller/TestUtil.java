package at.wambo.podcaster.controller;

import at.wambo.podcaster.auth.JwtResponse;
import at.wambo.podcaster.forms.CreateUserRequest;
import at.wambo.podcaster.model.LoginRequest;
import at.wambo.podcaster.model.RssFeed;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by martin on 19.11.16.
 */
public class TestUtil {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.findAndRegisterModules();
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> deserializePage(String json, Class<T> targetClass) throws IOException, ClassNotFoundException {
        JsonNode node = OBJECT_MAPPER.readTree(json);
        String content = OBJECT_MAPPER.writeValueAsString(node.get("content"));
        Class<T[]> targetArrayClass = (Class<T[]>) Class.forName("[L" + targetClass.getName() + ";");
        return Arrays.asList(OBJECT_MAPPER.readValue(content, targetArrayClass));
    }

    public static MvcResult registerUser(MockMvc mvc, String username, String password) throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail(username + "@gmail.com");
        request.setUsername(username);
        request.setPassword(password);
        request.setPasswordRepeated(password);
        String json = OBJECT_MAPPER.writeValueAsString(request);
        return mvc.perform(post("/auth/register")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    public static RssFeed addPodcast(MockMvc mvc, String token, String feedUrl) throws Exception {
        MvcResult result = mvc.perform(post("/api/feeds")
                .param("url", feedUrl)
                .header("Authorization", "Bearer " + token))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        return OBJECT_MAPPER.readValue(response, RssFeed.class);
    }

    public static String getToken(MockMvc mvc, String username, String password) throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername(username);
        login.setPassword(password);
        String json = OBJECT_MAPPER.writeValueAsString(login);
        MvcResult result = mvc.perform(post("/auth/token")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        JwtResponse response = OBJECT_MAPPER.readValue(content, JwtResponse.class);
        return response.getToken();
    }

}
