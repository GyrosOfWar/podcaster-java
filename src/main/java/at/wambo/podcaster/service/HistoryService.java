package at.wambo.podcaster.service;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.HistoryEntry;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.repository.HistoryEntryRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Martin
 *         02.12.2016
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HistoryService {
    private final @NonNull HistoryEntryRepository historyEntryRepository;

    public HistoryEntry addToHistory(User user, FeedItem item) {
        HistoryEntry entry = new HistoryEntry(item, user, ZonedDateTime.now(), 0);
        return historyEntryRepository.save(entry);
    }

    public Page<HistoryEntry> getHistoryForUser(User user, Pageable page) {
        List<HistoryEntry> entries = historyEntryRepository.getHistoryForUser(user, page).getContent();
        Map<LocalDate, List<HistoryEntry>> grouped = entries.stream()
                .collect(Collectors.groupingBy(e -> LocalDate.from(e.getTime())));

        return null;
    }
}
