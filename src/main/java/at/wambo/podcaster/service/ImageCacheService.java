package at.wambo.podcaster.service;

import at.wambo.podcaster.repository.FeedItemRepository;
import at.wambo.podcaster.repository.RssFeedRepository;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ImageCacheService {

  private static final Path BASE_PATH = Paths.get("thumbnails");

  private final FeedItemRepository itemRepository;
  private final RssFeedRepository rssFeedRepository;

  private byte[] resizeImage(URL url, int width) throws IOException {
    BufferedImage originalImage = ImageIO.read(url);
    double q = originalImage.getHeight() / (double) originalImage.getWidth();
    int height = (int) (width * q);
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    img.createGraphics()
        .drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
    log.debug("Resized image to {}x{} px", width, height);
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    ImageIO.write(img, "jpg", stream);
    return stream.toByteArray();
  }

  public byte[] getImage(int id, int width, ImageType type) throws IOException {
    String fileName = String.format("%s_%s_%s.jpeg", type.toString().toLowerCase(), id, width);
    Path path = BASE_PATH.resolve(fileName);
    File file = path.toFile();
    if (file.isFile()) {
      return Files.readAllBytes(path);
    } else {
      var url = findUrl(type, id);
      if (url.isPresent()) {
        byte[] image = resizeImage(url.get(), width);
        if (!BASE_PATH.toFile().isDirectory()) {
          Files.createDirectories(BASE_PATH);
        }
        Files.write(path, image);
        return image;
      } else {
        // TODO make fallback image
        throw new RuntimeException("Not yet implemented");
      }
    }
  }

  private Optional<URL> findUrl(ImageType type, int id) throws MalformedURLException {
    String url = null;
    switch (type) {
      case FEED:
        var feed = rssFeedRepository.findById(id).orElseThrow();
        url = feed.getImageUrl();
        break;
      case FEED_ITEM:
        var item = itemRepository.findById(id).orElseThrow();
        url = item.getImageUrl();
        break;
      default:
        throw new RuntimeException("Not implemented: " + type);
    }
    if (url != null) {
      return Optional.of(new URL(url));
    } else {
      return Optional.empty();
    }
  }

  public enum ImageType {
    FEED,
    FEED_ITEM
  }
}
