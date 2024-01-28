package book.store.repository.user;

import book.store.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<Object> findByEmail(String email);
}
