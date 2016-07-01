package at.wambo.podcaster;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Martin
 *         01.07.2016
 */
public interface FeedItemRepository extends CrudRepository<FeedItem, Integer> {
    FeedItem findByLink(String link);

    FeedItem findByhashedImageUrl(String hashedImageUrl);
}
