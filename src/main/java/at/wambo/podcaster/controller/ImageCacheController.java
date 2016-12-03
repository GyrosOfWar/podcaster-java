package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.RssFeed;
import at.wambo.podcaster.repository.FeedItemRepository;
import at.wambo.podcaster.repository.RssFeedRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping(path = "/api/images/")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ImageCacheController {
    private static final byte[] REDIS_KEY = "podcasterImageSet".getBytes();

    private static final Logger logger = LoggerFactory.getLogger(ImageCacheController.class);

    private final @NonNull RedisConnectionFactory connectionFactory;
    private final @NonNull FeedItemRepository itemRepository;
    private final @NonNull RssFeedRepository rssFeedRepository;

    // TODO save different image sizes
    private byte[] getImage(String hashedUrl, int width) {
        logger.debug("getImage: start");
        RedisConnection connection = null;
        try {
            connection = this.connectionFactory.getConnection();
            logger.debug("getImage: acquired Redis connection: {}", connection);
            byte[] result = connection.hGet(REDIS_KEY, hashedUrl.getBytes());
            if (result == null) {
                logger.debug("Found no image in Redis");
                URL url;
                List<FeedItem> items = this.itemRepository.findByHashedImageUrl(hashedUrl);
                try {
                    if (items.size() > 0) {
                        url = new URL(items.get(0).getImageUrl());
                    } else {
                        List<RssFeed> feeds = this.rssFeedRepository.findByHashedImageUrl(hashedUrl);
                        url = new URL(feeds.get(0).getImageUrl());
                    }
                    logger.debug("Found URL {} for hash {}", url, hashedUrl);
                    BufferedImage originalImage = ImageIO.read(url);
                    double q = originalImage.getHeight() / (double) originalImage.getWidth();
                    int height = (int) (width * q);
                    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    img.createGraphics().drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
                    logger.debug("Resized image");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    ImageIO.write(img, "jpg", stream);
                    byte[] imageBytes = stream.toByteArray();
                    connection.hSet(REDIS_KEY, hashedUrl.getBytes(), imageBytes);
                    logger.debug("Wrote image to Redis, returning");
                    return imageBytes;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else {
                logger.debug("Found image in redis, returing");
                return result;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @RequestMapping(path = "{hashedUrl}", produces = "image/jpeg")
    public byte[] serveImage(@PathVariable String hashedUrl, @RequestParam Integer size) {
        return getImage(hashedUrl, size);
    }
}
