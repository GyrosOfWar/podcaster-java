package at.wambo.podcaster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

/**
 * @author Martin
 *         02.12.2016
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "history")
public class HistoryEntry {
    @OneToOne
    private FeedItem feedItem;

    @OneToOne
    private User user;

    @Column(nullable = false)
    private Instant time;

    @Id
    @GeneratedValue
    private int id;
}
