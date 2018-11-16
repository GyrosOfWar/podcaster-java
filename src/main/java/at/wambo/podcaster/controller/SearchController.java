package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.repository.FeedItemRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Martin on 16.03.2017.
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SearchController {

  private final @NonNull
  FeedItemRepository feedItemRepository;

  @RequestMapping
  public Page<FeedItem> doSearch(@RequestParam("q") String query, Pageable pageable) {
    return feedItemRepository.search(query, pageable);
  }
}
