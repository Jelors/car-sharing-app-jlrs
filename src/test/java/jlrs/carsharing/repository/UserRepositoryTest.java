package jlrs.carsharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jlrs.carsharing.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("""
            Find user by email that = johnydepp1@gmail.com
            """)
    void findByEmail_ValidEmail_ReturnsUser() {
        User expected = new User();
        expected.setId(1L);
        expected.setEmail("johnydepp1@gmail.com");
        expected.setFirstName("Johny");
        expected.setLastName("Depp");
        expected.setPassword("carribeanPirate123");

        userRepository.save(expected);

        Optional<User> actual = userRepository.findByEmail(expected.getEmail());

        assertEquals(expected.getEmail(), actual.get().getEmail());
    }

    @Test
    @DisplayName("""
            Check if exists user with some special email
            """)
    void existsByEmail_ValidEmail_ReturnsTrue() {
        User expected = new User();
        expected.setId(1L);
        expected.setEmail("johnydepp1@gmail.com");
        expected.setFirstName("Johny");
        expected.setLastName("Depp");
        expected.setPassword("carribeanPirate123");

        userRepository.save(expected);

        assertTrue(userRepository.existsByEmail(expected.getEmail()));
    }
}
