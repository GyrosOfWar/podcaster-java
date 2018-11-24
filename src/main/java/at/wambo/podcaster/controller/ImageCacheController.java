package at.wambo.podcaster.controller;

import at.wambo.podcaster.service.ImageCacheService;
import at.wambo.podcaster.service.ImageCacheService.ImageType;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Martin 01.07.2016
 */
@RestController
@RequestMapping(path = "/api/images/")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ImageCacheController {

  private final ImageCacheService imageCacheService;

  @GetMapping(path = "feeds/{id}", produces = "image/jpeg")
  public byte[] serveFeedImage(@PathVariable int id, @RequestParam int size) throws IOException {
    return imageCacheService.getImage(id, size, ImageType.FEED);
  }

  @GetMapping(path = "feed_items/{id}", produces = "image/jpeg")
  public byte[] serveItemImage(@PathVariable int id, @RequestParam int size) throws IOException {
    return imageCacheService.getImage(id, size, ImageType.FEED_ITEM);
  }
}
