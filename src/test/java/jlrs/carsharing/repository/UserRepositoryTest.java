package jlrs.carsharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jlrs.carsharing.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/clear-all-info.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/insert/insert-users-to-users-table.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/clear-all-info.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("""
            Find user by email that = johnydepp1@gmail.com
            """)
    void findByEmail_ValidEmail_ReturnsUser() {
        String expectedEmail = "charlesBill@example.com";
        Optional<User> actual = userRepository.findByEmail(expectedEmail);

        assertEquals(expectedEmail, actual.get().getEmail());
    }

    @Test
    @DisplayName("""
            Check if exists user with some special email
            """)
    void existsByEmail_ValidEmail_ReturnsTrue() {
        String expectedEmail = "johnSmith@example.com";

        assertTrue(userRepository.existsByEmail(expectedEmail));
    }
}
