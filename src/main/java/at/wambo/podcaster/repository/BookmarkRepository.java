package at.wambo.podcaster.repository;

import at.wambo.podcaster.model.Bookmark;
import org.springframework.data.repository.CrudRepository;

public interface BookmarkRepository extends CrudRepository<Bookmark, Integer> {

}
