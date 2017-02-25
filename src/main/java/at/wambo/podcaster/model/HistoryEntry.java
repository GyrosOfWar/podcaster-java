package at.wambo.podcaster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

/**
 * @author Martin
 *         02.12.2016
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "history")
@NamedNativeQuery(name = "HistoryEntry.historyForUser",
        query = "SELECT * FROM history h GROUP BY date(h.time)",
        resultClass = HistoryEntry.class)
public class HistoryEntry {
    @OneToOne
    private FeedItem feedItem;

    @OneToOne
    @JsonIgnoreProperties("history")
    private User user;

    @Column(nullable = false)
    private ZonedDateTime time;

    @Id
    @GeneratedValue
    private int id;
}
