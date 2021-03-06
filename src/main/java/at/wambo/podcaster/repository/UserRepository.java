package at.wambo.podcaster.repository;

import at.wambo.podcaster.model.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Martin 01.07.2016
 */
public interface UserRepository extends CrudRepository<User, Integer> {

  Optional<User> findByName(String name);

  Optional<User> findByEmail(String email);
}
