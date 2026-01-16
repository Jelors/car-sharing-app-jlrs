package jlrs.carsharing.repository;

import java.util.Optional;
import jlrs.carsharing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
