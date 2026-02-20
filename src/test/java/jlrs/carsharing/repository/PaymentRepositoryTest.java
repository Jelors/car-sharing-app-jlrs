package jlrs.carsharing.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jlrs.carsharing.model.Payment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/insert/insert-users-to-users-table.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/insert/insert-rentals-to-rentals-table.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/insert/insert-payments-to-payments-table.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:database/clear-all-info.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("""
            Find all payments by user id
            """)
    void findAllByRental_User_Id_ValidInput_ShouldReturnTwoPayments() {
        int expectedPaymentsSize = 2;

        List<Payment> actualPaymentList = paymentRepository.findAllByRental_User_Id(2L);

        assertEquals(expectedPaymentsSize, actualPaymentList.size());
    }

    @Test
    @DisplayName("""
            Find payment by session id
            """)
    void findBySessionId_ValidInput_ShouldReturnPaymentWithIdTwo() {
        String sessionId = "cs_test_B9876543210asdfghjklqwertyuiopzxcvbnm123";
        BigDecimal expectedTotal = BigDecimal.valueOf(4600).setScale(2, RoundingMode.HALF_UP);
        Optional<Payment> actualPayment = paymentRepository.findBySessionId(sessionId);

        assertEquals(expectedTotal, actualPayment.get().getTotal());
    }

}
