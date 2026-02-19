package jlrs.carsharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jlrs.carsharing.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRoleRepositoryTest {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    @DisplayName("""
            Find role by name
            """)
    void findByRole_ValidInput_ReturnsRole() {
        UserRole expectedRole = new UserRole();
        expectedRole.setId(1L);
        expectedRole.setRole(UserRole.RoleName.MANAGER);

        userRoleRepository.save(expectedRole);

        Optional<UserRole> actualRole = userRoleRepository.findByRole(expectedRole.getRole());

        assertEquals(expectedRole.getRole(), actualRole.get().getRole());
    }
}
