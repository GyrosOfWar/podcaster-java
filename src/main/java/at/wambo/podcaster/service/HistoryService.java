package at.wambo.podcaster.service;

import at.wambo.podcaster.model.FeedItem;
import at.wambo.podcaster.model.HistoryEntry;
import at.wambo.podcaster.model.User;
import at.wambo.podcaster.repository.HistoryEntryRepository;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author Martin 02.12.2016
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HistoryService {

  private final @NonNull
  HistoryEntryRepository historyEntryRepository;

  public Page<GroupedHistoryEntries> getGroupedHistory(User user, Pageable page) {
    Page<HistoryEntry> entries = getHistoryForUser(user, page);
    Map<LocalDate, List<HistoryEntry>> grouped = entries.getContent().stream()
        .collect(Collectors.groupingBy(e -> e.getTime().toLocalDate()));

    List<GroupedHistoryEntries> groupedEntries = new ArrayList<>();
    grouped.forEach((d, e) -> {
      groupedEntries.add(new GroupedHistoryEntries(d, e));
    });
    Comparator<GroupedHistoryEntries> comparator = Comparator
        .comparing(GroupedHistoryEntries::getDate);
    groupedEntries.sort(comparator.reversed());
    return new PageImpl<>(groupedEntries, page, entries.getTotalElements());
  }

  public void addToHistory(User user, FeedItem item) {
    HistoryEntry last = historyEntryRepository.findFirstByUserOrderByTimeDesc(user);
    if (last != null && last.getFeedItem().equals(item)) {
      last.setTime(ZonedDateTime.now());
      historyEntryRepository.save(last);
    } else {
      HistoryEntry entry = new HistoryEntry(item, user, ZonedDateTime.now(), 0);
      historyEntryRepository.save(entry);
    }
  }

  public Page<HistoryEntry> getHistoryForUser(User user, Pageable page) {
    return historyEntryRepository.getHistoryForUser(user, page);
  }

  public void deleteForFeed(Integer id) {
    historyEntryRepository.deleteAll(historyEntryRepository.findByFeedItemFeedId(id));
  }

  @Value
  public static class GroupedHistoryEntries {

    private LocalDate date;
    private List<HistoryEntry> entries;
  }
}
