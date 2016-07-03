package at.wambo.podcaster.repository;


import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.RssFeed;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Martin
 *         01.07.2016
 */
public interface RssFeedRepository extends PagingAndSortingRepository<RssFeed, Integer> {
    List<FeedItem> fullTextSearch(Integer id, String query);
}
