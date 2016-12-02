package at.wambo.podcaster.util;

import at.wambo.podcaster.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Martin
 *         02.12.2016
 */
public class Util {
    public static User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
