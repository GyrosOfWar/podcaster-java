package at.wambo.podcaster;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;
import org.jdom2.Element;

import javax.persistence.*;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author Martin
 *         01.07.2016
 */
@Data
@Entity
@Table(name = "feed_items")
@ToString(exclude = "feed")
public class FeedItem {
    @GeneratedValue
    @Id
    private int id;
    private String title;
    private String link;
    private String description;
    private String mp3Url;
    private ZonedDateTime pubDate;
    private Duration duration;
    @ManyToOne(targetEntity = RssFeed.class)
    @JsonIgnore
    private RssFeed feed;
    private Duration lastPosition;
    private String imageUrl;
    @OneToOne(targetEntity = User.class)
    private User owner;
    private boolean isFavorite;
    private String hashedImageUrl;

    public static FeedItem fromEntry(RssFeed feed, SyndEntry entry) {
        FeedItem item = new FeedItem();
        item.setTitle(entry.getTitle());
        item.setLink(entry.getLink());
        item.setDescription(entry.getDescription().getValue());
        String mp3Url = null;
        Duration duration = Duration.ZERO;
        for (SyndEnclosure enc : entry.getEnclosures()) {
            mp3Url = enc.getUrl();
            duration = Duration.ofSeconds(enc.getLength());
        }
        item.setMp3Url(mp3Url);
        // item.setOwner(owner);
        item.setDuration(duration);
        item.setLink(entry.getLink());
        Date pubDate = entry.getPublishedDate();
        item.setPubDate(ZonedDateTime.ofInstant(pubDate.toInstant(), ZoneId.of("UTC")));
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
}
