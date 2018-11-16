package at.wambo.podcaster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Duration;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Martin 26.06.2017
 */
@Data
@Entity
@Table(name = "bookmarks")
@AllArgsConstructor
@NoArgsConstructor
public class Bookmark {

  @GeneratedValue
  @Id
  private Integer id;

  @Column(nullable = false)
  private Duration position;

  @ManyToOne(targetEntity = User.class, optional = false)
  private User user;

  @ManyToOne(targetEntity = FeedItem.class, optional = false)
  @JsonIgnoreProperties("bookmarks")
  private FeedItem feedItem;
}
