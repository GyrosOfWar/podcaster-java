package at.wambo.podcaster.repository;

import at.wambo.podcaster.model.Bookmark;
import at.wambo.podcaster.model.FeedItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Martin
 *         01.07.2016
 */
public interface FeedItemRepository extends PagingAndSortingRepository<FeedItem, Integer> {
    FeedItem findByLink(String link);

    List<FeedItem> findByHashedImageUrl(String hashedImageUrl);

    Page<FeedItem> findByFeedIdOrderByPubDateDesc(Integer feedId, Pageable pageable);

    @Query(name = "FeedItem.search")
    List<FeedItem> search(String query);

    List<FeedItem> findByFeedId(Integer id);

    Optional<FeedItem> findById(Integer id);

    void deleteByFeedId(Integer id);
}
