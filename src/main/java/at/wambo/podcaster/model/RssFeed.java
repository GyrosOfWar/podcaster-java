package at.wambo.podcaster.model;

import at.wambo.podcaster.repository.FeedItemRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.net.MalformedURLException;
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
@Table(name = "feeds", uniqueConstraints = {@UniqueConstraint(columnNames = "feedUrl")})
@NamedNativeQuery(name = "RssFeed.fullTextSearch",
        query = "SELECT * FROM feed_items WHERE feed_id = ?1 AND to_tsvector('english', title || ' ' || description) @@ to_tsquery(?2)",
        resultClass = FeedItem.class)
public class RssFeed {
    private static final Logger logger = LoggerFactory.getLogger(RssFeed.class);

    @GeneratedValue
    @Id
    private int id;
    private String feedUrl;
    private String title;
    private String imageUrl;
    @ManyToOne(targetEntity = User.class, optional = false)
    private User owner;
    private String hashedImageUrl;
    @OneToMany(targetEntity = FeedItem.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<FeedItem> items;

    public static RssFeed fromUrl(String urlString, User user) {
        URL url;
        try {
            url = fixUrl(URLDecoder.decode(urlString, "UTF-8"));
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            throw new RuntimeException(e);
        }

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed;
        try {
            feed = input.build(new XmlReader(url));
        } catch (FeedException | IOException e) {
            throw new RuntimeException(e);
        }

        RssFeed f = new RssFeed();
        String imageUrl = feed.getImage().getUrl();
        f.setFeedUrl(url.toString());
        f.setImageUrl(imageUrl);
        f.setTitle(feed.getTitle());
        List<FeedItem> items = feed.getEntries()
                .stream()
                .map(e -> FeedItem.fromEntry(f, e, user))
                .collect(Collectors.toList());
        f.setItems(items);
        String hash = DigestUtils.sha256Hex(imageUrl);
        f.setHashedImageUrl(hash);
        f.setOwner(user);
        return f;
    }

    private static URL fixUrl(String url) throws MalformedURLException {
        if (url.startsWith("http")) {
            return new URL(url);
        } else {
            return new URL("http://" + url);
        }
    }

    public List<FeedItem> refresh(FeedItemRepository itemRepository, User user) {
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
                item = FeedItem.fromEntry(this, entry, user);
                itemRepository.save(item);
                newItems.add(item);
            }
        }
        return newItems;
    }
}
