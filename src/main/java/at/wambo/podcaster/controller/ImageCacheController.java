package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.repository.FeedItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * @author Martin
 *         01.07.2016
 */
@RestController
@RequestMapping(path = "/api/image/")
public class ImageCacheController {
    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Autowired
    private FeedItemRepository itemRepository;

    // TODO save different image sizes
    private byte[] getImage(String hashedUrl, int size) {
        RedisConnection connection = this.connectionFactory.getConnection();
        byte[] result = connection.get(hashedUrl.getBytes());
        if (result == null) {
            FeedItem item = this.itemRepository.findByHashedImageUrl(hashedUrl).get(0);
            URL url;
            try {
                url = new URL(item.getImageUrl());
                BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
                img.createGraphics().drawImage(ImageIO.read(url).getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, null);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(img, "jpg", stream);
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

    @RequestMapping(path = "{hashedUrl}", produces = "image/jpeg")
    public byte[] serveImage(@PathVariable String hashedUrl, @RequestParam Integer size) {
        return getImage(hashedUrl, size);
    }
}
