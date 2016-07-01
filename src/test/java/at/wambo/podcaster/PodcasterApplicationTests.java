package at.wambo.podcaster;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PodcasterApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class PodcasterApplicationTests {

    @Test
    public void contextLoads() {
    }

}
