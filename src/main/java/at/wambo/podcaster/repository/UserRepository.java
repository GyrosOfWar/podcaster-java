package at.wambo.podcaster.repository;

import at.wambo.podcaster.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * @author Martin
 *         01.07.2016
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}
