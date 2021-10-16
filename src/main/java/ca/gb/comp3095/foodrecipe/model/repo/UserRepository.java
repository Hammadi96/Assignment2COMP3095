package ca.gb.comp3095.foodrecipe.model.repo;

import ca.gb.comp3095.foodrecipe.model.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    /**
     * Uses {@link Optional} as return and parameter type.
     *
     * @param username
     * @return
     */
    Optional<User> findByName(String username);

    /**
     * Find all users with the given username. This method will be translated into a query by constructing it directly
     * from the method name as there is no other query declared.
     *
     * @param username
     * @return
     */
    List<User> findAllByName(String username);

}
