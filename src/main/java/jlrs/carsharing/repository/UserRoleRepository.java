package jlrs.carsharing.repository;

import java.util.Optional;
import jlrs.carsharing.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByRole(UserRole.RoleName role);
}
