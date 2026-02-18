package jlrs.carsharing.repository;

import jlrs.carsharing.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Find user by email")
    void findByEmail_CorrectInput_ShouldReturnUser() {
        User expected = new User();
        expected.setFirstName("Johnny");
        expected.setLastName("Depp");
        expected.setEmail("johnny1depp@gmail.com");
        expected.setPassword("pirateOfTheCarribean");

        userRepository.save(expected);

        Optional<User> actual = userRepository.findByEmail("johnny1depp@gmail.com");

        assertEquals(expected.getEmail(), actual.get().getEmail());
    }
}
