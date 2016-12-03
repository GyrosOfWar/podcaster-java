package at.wambo.podcaster.requests;

import lombok.Data;

import java.time.Duration;

/**
 * Created by martin on 03.12.16.
 */
@Data
public class ChangeFeedItemRequest {
    private boolean favorite;
    private Duration lastPosition;
}
