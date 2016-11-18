package at.wambo.podcaster.forms;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author martin
 *         Created on 01.07.16.
 */
@Data
public class CreateUserRequest {
    @NotEmpty
    private String email;
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String passwordRepeated;

    public boolean hasNullValues() {
        return email == null || username == null || password == null || passwordRepeated == null;
    }
}
