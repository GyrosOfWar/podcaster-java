package at.wambo.podcaster.repository;

import at.wambo.podcaster.model.FeedItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Martin 01.07.2016
 */
public interface FeedItemRepository extends PagingAndSortingRepository<FeedItem, Integer> {

  Page<FeedItem> findByFeedIdOrderByPubDateDesc(Integer feedId, Pageable pageable);

  @Query(value =
      "SELECT *, ts_rank_cd(to_tsvector('english', title || ' ' || description), plainto_tsquery('english', ?1)) AS ranking "
          +
          "FROM feed_items " +
          "WHERE plainto_tsquery('english', ?1) @@ to_tsvector('english', title || ' ' || description) "
          +
          "ORDER BY ?#{#pageable}",
      countQuery = "SELECT count(*) " +
          "FROM feed_items " +
          "WHERE plainto_tsquery('english', ?1) @@ to_tsvector('english', title || ' ' || description) ",
      nativeQuery = true)
  Page<FeedItem> search(String query, Pageable pageable);

  List<FeedItem> findByFeedId(Integer id);

  Optional<FeedItem> findById(Integer id);

  void deleteByFeedId(Integer id);

  List<FeedItem> findByGuid(String guid);

  List<FeedItem> findByTitle(String title);

  List<FeedItem> findByLink(String link);
}
