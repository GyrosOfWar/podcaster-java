package at.wambo.podcaster.repository;

import at.wambo.podcaster.model.FeedItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Martin
 *         01.07.2016
 */
public interface FeedItemRepository extends PagingAndSortingRepository<FeedItem, Integer> {
    FeedItem findByLink(String link);

    List<FeedItem> findByHashedImageUrl(String hashedImageUrl);

    @Query(name = "FeedItem.findPaginated")
    List<FeedItem> findByFeedIdPaginated(Integer feedId, Integer offset, Integer count);
}
