package at.wambo.podcaster.configuration;

import at.wambo.podcaster.model.User;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * @author Martin
 *         13.08.2016
 */
public class CurrentUser extends org.springframework.security.core.userdetails.User {
    public static final String USER_ROLE = "user";
    private User user;

    public CurrentUser(User user) {
        super(user.getEmail(), user.getPwHash(), AuthorityUtils.createAuthorityList(USER_ROLE));
        this.user = user;
    }
}
