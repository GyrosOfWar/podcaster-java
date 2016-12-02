package at.wambo.podcaster.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

/**
 * @author Martin
 *         02.12.2016
 */
@Data
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
