package at.wambo.podcaster.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Martin 02.12.2016
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
  @JsonIgnoreProperties("history")
  private User user;

  @Column(nullable = false)
  private ZonedDateTime time;

  @Id
  @GeneratedValue
  private int id;
}
