package at.wambo.podcaster.controller;

import at.wambo.podcaster.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Martin
 *         13.08.2016
 */

@RestController
public class UserController {
    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${jwt.secret}")
    private String secret;

    @RequestMapping(path = "/api/user", method = RequestMethod.GET)
    public User getUserInfo() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
