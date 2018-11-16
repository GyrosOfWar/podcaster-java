package at.wambo.podcaster.requests;

import lombok.Data;

/**
 * Created by martin on 18.11.16.
 */
@Data
public class LoginRequest {

  private String username;
  private String password;
}
