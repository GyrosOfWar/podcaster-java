package at.wambo.podcaster.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Data;
import org.hibernate.annotations.BatchSize;

/**
 * @author Martin 01.07.2016
 */
@Data
@Entity
@Table(name = "feeds", uniqueConstraints = {@UniqueConstraint(columnNames = "feedUrl")})
@NamedNativeQuery(name = "RssFeed.fullTextSearch",
    query = "SELECT * FROM feed_items WHERE feed_id = ?1 AND to_tsvector('english', title || ' ' || description) @@ to_tsquery(?2)",
    resultClass = FeedItem.class)
public class RssFeed {

  @GeneratedValue
  @Id
  private int id;

  @Column(nullable = false)
  private String feedUrl;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String imageUrl;

  @ManyToOne(targetEntity = User.class, optional = false)
  private User owner;

  @OneToMany(targetEntity = FeedItem.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @BatchSize(size = 20)
  @JsonIgnore
  private List<FeedItem> items;

  @Column
  private String lastETag;
}
