package at.wambo.podcaster;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Martin
 *         01.07.2016
 */
public interface UserRepository extends CrudRepository<User, Integer> {
}
