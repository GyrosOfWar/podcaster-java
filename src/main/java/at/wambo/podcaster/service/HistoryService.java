package at.wambo.podcaster.service;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.HistoryEntry;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.repository.HistoryEntryRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * @author Martin
 *         02.12.2016
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HistoryService {
    private final @NonNull HistoryEntryRepository historyEntryRepository;

    public HistoryEntry addToHistory(User user, FeedItem item) {
        HistoryEntry entry = new HistoryEntry();
        entry.setFeedItem(item);
        entry.setUser(user);
        entry.setTime(Instant.now());
        return historyEntryRepository.save(entry);
    }

}
