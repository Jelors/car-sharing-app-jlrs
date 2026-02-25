package jlrs.carsharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.transaction.Transactional;
import jlrs.carsharing.model.Rental;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/insert/insert-info.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/clear-all-info.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class RentalRepositoryTest {
    @Autowired
    private RentalRepository rentalRepository;

    @Test
    @DisplayName("""
            Find all rentals by user ID and their status, returns LIST
            """)
    void findAllByUserIdAndIsActive_UserIdIsTwoAndStatusIsActive_ShouldReturnOneRental() {
        int expectedRentalListSize = 1;
        List<Rental> actualRentalList = rentalRepository.findAllByUserIdAndIsActive(2L, true);

        assertEquals(expectedRentalListSize, actualRentalList.size());
    }

    @Test
    @DisplayName("""
            Find specific rental by user id and rental id
            """)
    void findByUserIdAndRentalId_ValidInput_ShouldReturnUnactiveRental() {
        boolean expectedRentalStatus = false; // inactive rental

        Optional<Rental> actualRental = rentalRepository.findByUserIdAndId(2L, 1L);

        assertEquals(expectedRentalStatus, actualRental.get().isActive());
    }

    @Test
    @DisplayName("""
            Find all rentals by their status
            """)
    void findAllByIsActive_ValidInput_StatusIsActive() {
        int expectedRentalsListSize = 2;

        List<Rental> actualRentalList = rentalRepository.findAllByIsActive(true);

        assertEquals(expectedRentalsListSize, actualRentalList.size());
    }

    @Test
    @DisplayName("""
            Find all rentals whose actual return status is currently null and custom break date
            """)
    void findAllByReturnDateLessThanEqualAndActualReturnDateIsNull_ValidInput_ShouldReturnTwoRentals() {
        int expectedRentalsListSize = 2;
        LocalDate date = LocalDate.of(2026, 02, 22);
        List<Rental> actualRentalList = rentalRepository.findAllByReturnDateLessThanEqualAndActualReturnDateIsNull(date);

        assertEquals(expectedRentalsListSize, actualRentalList.size());
    }

}
