package at.wambo.podcaster.requests;

import java.time.Duration;
import lombok.Data;

/**
 * Created by martin on 03.12.16.
 */
@Data
public class ChangeFeedItemRequest {

  private boolean favorite;
  private Duration lastPosition;
}
