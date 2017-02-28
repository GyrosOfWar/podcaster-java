package at.wambo.podcaster.repository;

import at.wambo.podcaster.model.HistoryEntry;
import at.wambo.podcaster.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * @author Martin
 *         02.12.2016
 */
public interface HistoryEntryRepository extends CrudRepository<HistoryEntry, Integer> {
    @Query("from HistoryEntry h where h.user = :user order by h.time desc")
    Page<HistoryEntry> getHistoryForUser(@Param("user") User user, Pageable page);
}
