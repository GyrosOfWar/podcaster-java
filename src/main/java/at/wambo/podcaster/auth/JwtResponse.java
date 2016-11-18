package at.wambo.podcaster.auth;

import lombok.Data;

/**
 * Created by martin on 18.11.16.
 */
@Data
public class JwtResponse {
    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }
}
