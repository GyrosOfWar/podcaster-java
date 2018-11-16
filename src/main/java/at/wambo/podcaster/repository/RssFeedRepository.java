package at.wambo.podcaster.repository;


import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.RssFeed;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Martin 01.07.2016
 */
public interface RssFeedRepository extends PagingAndSortingRepository<RssFeed, Integer> {

  @Query(name = "RssFeed.fullTextSearch")
  List<FeedItem> fullTextSearch(Integer id, String query);

  List<RssFeed> findByHashedImageUrl(String hashedImageUrl);

  @Query("from FeedItem i where i.feed = :feedId and i.isFavorite = true")
  List<FeedItem> findFavoriteItems(@Param("feedId") Integer feedId);

  Optional<RssFeed> findById(Integer id);

  void deleteById(Integer id);
}
