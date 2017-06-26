package at.wambo.podcaster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;
import org.jdom2.Element;

import javax.persistence.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * @author Martin
 *         01.07.2016
 */
@Data
@Entity
@Table(name = "feed_items")
@ToString(exclude = "feed")
@NamedNativeQuery(name = "FeedItem.search",
        query = "SELECT *, ts_rank_cd(to_tsvector('english', title || ' ' || description), to_tsquery('english', ?1)) AS ranking " +
                "FROM feed_items " +
                "WHERE to_tsquery('english', ?1) @@ to_tsvector('english', title || ' ' || description) " +
                "ORDER BY ranking DESC",
        resultClass = FeedItem.class)
public class FeedItem {
    private static final long MAX_LENGTH = 60 * 60 * 10;

    @GeneratedValue
    @Id
    private int id;
    private String title;
    private String link;
    @Column(columnDefinition = "varchar")
    private String description;
    private String mp3Url;
    private Instant pubDate;
    private Duration duration;
    @ManyToOne(targetEntity = RssFeed.class)
    @JsonIgnoreProperties({"items", "owner"})
    private RssFeed feed;
    private Duration lastPosition;
    private String imageUrl;
    @OneToOne(targetEntity = User.class)
    private User owner;
    private boolean isFavorite;
    private String hashedImageUrl;
    @OneToMany(targetEntity = Bookmark.class)
    private List<Bookmark> bookmarks;

    static FeedItem fromEntry(RssFeed feed, SyndEntry entry, User owner) {
        FeedItem item = new FeedItem();
        item.setTitle(entry.getTitle());
        item.setLink(entry.getLink());
        item.setDescription(entry.getDescription().getValue());
        String mp3Url = null;
        Duration duration = Duration.ZERO;
        for (SyndEnclosure enc : entry.getEnclosures()) {
            mp3Url = enc.getUrl();
            long seconds = enc.getLength();
            // Guess if the length attribute is the length in bytes or the length in seconds.
            if (seconds < MAX_LENGTH) {
                duration = Duration.ofSeconds(seconds);
            } else {
                for (Element el: entry.getForeignMarkup()) {
                    if ("duration".equals(el.getName())) {
                        String value = el.getText();
                        try {
                            seconds = Long.parseLong(value);
                            duration = Duration.ofSeconds(seconds);
                        } catch (NumberFormatException ignored) {
                            duration = parseDuration(value);
                        }
                    }
                }
            }

        }
        item.setMp3Url(mp3Url);
        item.setOwner(owner);
        item.setDuration(duration);
        item.setLink(entry.getLink());
        Date pubDate = entry.getPublishedDate();
        item.setPubDate(pubDate.toInstant());
        item.setLastPosition(Duration.ZERO);
        item.setFeed(feed);
        String imageUrl = null;

        for (Element el : entry.getForeignMarkup()) {
            if ("image".equals(el.getName())) {
                imageUrl = el.getAttribute("href").getValue();
            }

        }
        item.setImageUrl(imageUrl);
        String hash = DigestUtils.sha256Hex(imageUrl);
        item.setHashedImageUrl(hash);

        return item;
    }

    private static Duration parseDuration(String value) {
        String[] tokens = value.split(":");
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        if (tokens.length == 3) {
            hours = Integer.parseInt(tokens[0]);
            minutes = Integer.parseInt(tokens[1]);
            seconds = Integer.parseInt(tokens[2]);
        }
        if (tokens.length == 2) {
            minutes = Integer.parseInt(tokens[0]);
            seconds = Integer.parseInt(tokens[1]);
        }

        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }
}
