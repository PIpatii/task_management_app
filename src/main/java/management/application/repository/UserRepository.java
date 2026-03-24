package management.application.repository;

import java.util.Optional;
import management.application.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    User getUserByEmail(String username);

    User getUserById(Long id);

    String email(String email);
}
