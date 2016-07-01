package at.wambo.podcaster.repository;


import at.wambo.podcaster.model.RssFeed;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Martin
 *         01.07.2016
 */
public interface RssFeedRepository extends PagingAndSortingRepository<RssFeed, Integer> {

}
