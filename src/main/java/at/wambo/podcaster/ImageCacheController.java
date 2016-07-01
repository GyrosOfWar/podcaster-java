package at.wambo.podcaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * @author Martin
 *         01.07.2016
 */
@RestController
public class ImageCacheController {
    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Autowired
    private FeedItemRepository itemRepository;

    public ImageCacheController() {

    }

    private byte[] getImage(String hashedUrl, int size) {
        RedisConnection connection = connectionFactory.getConnection();
        byte[] result = connection.get(hashedUrl.getBytes());
        if (result == null) {
            FeedItem item = itemRepository.findByhashedImageUrl(hashedUrl);
            URL url;
            try {
                url = new URL(item.getImageUrl());
                BufferedImage image = ImageIO.read(url);
                BufferedImage thumbnail = (BufferedImage) image.getScaledInstance(size, size, BufferedImage.SCALE_SMOOTH);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(thumbnail, "jpg", stream);
                byte[] imageBytes = stream.toByteArray();
                connection.set(hashedUrl.getBytes(), imageBytes);
                return imageBytes;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            return result;
        }
    }

    @RequestMapping(path = "/api/image/{hashedUrl}")
    public byte[] serveImage(@PathVariable String hashedUrl, @RequestParam Integer size) {
        return getImage(hashedUrl, size);
    }

    public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setItemRepository(FeedItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }
}
