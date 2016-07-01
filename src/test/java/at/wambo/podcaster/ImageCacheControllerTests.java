package at.wambo.podcaster;

import at.wambo.podcaster.controller.ImageCacheController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Martin
 *         01.07.2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PodcasterApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class ImageCacheControllerTests {
    @Autowired
    private ImageCacheController controller;

    @Test
    public void cacheImage() {

    }
}
