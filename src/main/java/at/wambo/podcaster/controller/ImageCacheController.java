package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.RssFeed;
import at.wambo.podcaster.repository.FeedItemRepository;
import at.wambo.podcaster.repository.RssFeedRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
import java.util.List;

/**
 * @author Martin
 *         01.07.2016
 */
@RestController
@RequestMapping(path = "/api/image/")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ImageCacheController {
    private static final byte[] REDIS_KEY = "podcasterImageSet".getBytes();

    private final @NonNull RedisConnectionFactory connectionFactory;
    private final @NonNull FeedItemRepository itemRepository;
    private final @NonNull RssFeedRepository rssFeedRepository;

    // TODO save different image sizes
    private byte[] getImage(String hashedUrl, int size) {
        RedisConnection connection = this.connectionFactory.getConnection();
        byte[] result = connection.hGet(REDIS_KEY, hashedUrl.getBytes());
        if (result == null) {
            URL url;
            List<FeedItem> items = this.itemRepository.findByHashedImageUrl(hashedUrl);
            try {
                if (items.size() > 0) {
                    url = new URL(items.get(0).getImageUrl());
                } else {
                    List<RssFeed> feeds = this.rssFeedRepository.findByHashedImageUrl(hashedUrl);
                    url = new URL(feeds.get(0).getImageUrl());
                }

                BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
                img.createGraphics().drawImage(ImageIO.read(url).getScaledInstance(size, size, Image.SCALE_SMOOTH), 0, 0, null);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(img, "jpg", stream);
                byte[] imageBytes = stream.toByteArray();
                connection.hSet(REDIS_KEY, hashedUrl.getBytes(), imageBytes);
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
