package at.wambo.podcaster;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Martin
 *         01.07.2016
 */
@Data
@Entity
@Table(name = "feeds")
public class RssFeed {
    private static final Logger logger = LoggerFactory.getLogger(RssFeed.class);

    @GeneratedValue
    @Id
    private int id;
    private String feedUrl;
    private String title;
    private String imageUrl;
    @ManyToOne(targetEntity = User.class)
    private User owner;
    private String hashedImageUrl;
    @OneToMany(targetEntity = FeedItem.class, cascade = CascadeType.ALL)
    private List<FeedItem> items;

    public static RssFeed fromUrl(String url) {
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed;
        try {
            feed = input.build(new XmlReader(new URL(url)));
        } catch (FeedException | IOException e) {
            logger.error("Error fetching or decoding feed: ", e);
            return null;
        }

        RssFeed f = new RssFeed();
        // f.setOwner(owner);
        String imageUrl = feed.getImage().getUrl();
        f.setFeedUrl(url);
        f.setImageUrl(imageUrl);
        f.setTitle(feed.getTitle());
        List<FeedItem> items = feed.getEntries()
                .stream()
                .map(e -> FeedItem.fromEntry(f, e))
                .collect(Collectors.toList());
        f.setItems(items);
        String hash = DigestUtils.sha256Hex(imageUrl);
        f.setHashedImageUrl(hash);
        return f;
    }

    public List<FeedItem> refresh(FeedItemRepository itemRepository) {
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed;
        try {
            feed = input.build(new XmlReader(new URL(this.feedUrl)));
        } catch (FeedException | IOException e) {
            throw new RuntimeException(e);
        }
        List<FeedItem> newItems = new ArrayList<>();

        for (SyndEntry entry : feed.getEntries()) {
            FeedItem item = itemRepository.findByLink(entry.getLink());
            if (item == null) {
                item = FeedItem.fromEntry(this, entry);
                itemRepository.save(item);
                newItems.add(item);
            }
        }
        return newItems;
    }
}
