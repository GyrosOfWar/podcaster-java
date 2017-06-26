package at.wambo.podcaster.model;

import lombok.Data;

import javax.persistence.*;
import java.time.Duration;

/**
 * @author Martin
 *         26.06.2017
 */
@Data
@Entity
@Table(name = "bookmarks")
public class Bookmark {
    @GeneratedValue
    @Id
    private int id;
    private Duration duration;
    @ManyToOne(targetEntity = User.class)
    private User user;
    @ManyToOne(targetEntity = FeedItem.class)
    private FeedItem feedItem;
}
