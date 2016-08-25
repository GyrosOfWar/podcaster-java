package at.wambo.podcaster.configuration;

import at.wambo.podcaster.model.User;
import at.wambo.podcaster.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Martin
 *         13.08.2016
 */
@Service
public class CurrentUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public CurrentUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByName(username).orElseThrow(() -> new UsernameNotFoundException(String.format("User with username %s not found", username)));
        return new CurrentUser(user);
    }
}
