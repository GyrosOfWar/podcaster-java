package at.wambo.podcaster.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.Assert.assertTrue;

/**
 * @author Martin Tomasi.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void registerUser() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("email", "martin.tomasi@gmail.com");
        params.add("username", "martin");
        params.add("password", "test");
        params.add("passwordRepeated", "test");
        ResponseEntity<String> entity = restTemplate.postForEntity("/register", params, String.class);
        assertTrue(entity.getStatusCode().is3xxRedirection());
    }
}
