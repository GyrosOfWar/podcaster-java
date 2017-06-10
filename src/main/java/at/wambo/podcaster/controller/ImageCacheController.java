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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Martin
 *         01.07.2016
 */
@RestController
@RequestMapping(path = "/api/images/")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ImageCacheController {
    private static final Logger logger = LoggerFactory.getLogger(ImageCacheController.class);
    private static final Path BASE_PATH = Paths.get("thumbnails");

    private final @NonNull FeedItemRepository itemRepository;
    private final @NonNull RssFeedRepository rssFeedRepository;

    private byte[] resizeImage(URL url, int width) throws IOException {
        BufferedImage originalImage = ImageIO.read(url);
        double q = originalImage.getHeight() / (double) originalImage.getWidth();
        int height = (int) (width * q);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        img.createGraphics().drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        logger.debug("Resized image to {}x{} px", width, height);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", stream);
        return stream.toByteArray();
    }

    private byte[] getImage(String hashedUrl, int width) {
        String fileName = hashedUrl + "_" + width + ".jpeg";
        Path path = BASE_PATH.resolve(fileName);
        File file = path.toFile();
        try {
            if (file.isFile()) {
                return Files.readAllBytes(path);
            } else {
                URL url;
                List<FeedItem> items = this.itemRepository.findByHashedImageUrl(hashedUrl);
                if (items.size() > 0) {
                    url = new URL(items.get(0).getImageUrl());
                } else {
                    List<RssFeed> feeds = this.rssFeedRepository.findByHashedImageUrl(hashedUrl);
                    if (feeds.size() == 0) {
                        return null;
                    }
                    url = new URL(feeds.get(0).getImageUrl());
                }
                byte[] image = resizeImage(url, width);
                if (!BASE_PATH.toFile().isDirectory()) {
                    Files.createDirectories(BASE_PATH);
                }
                Files.write(path, image);
                return image;
            }

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @RequestMapping(path = "{hashedUrl}", produces = "image/jpeg")
    public byte[] serveImage(@PathVariable String hashedUrl, @RequestParam Integer size) {
        return getImage(hashedUrl, size);
    }
}
