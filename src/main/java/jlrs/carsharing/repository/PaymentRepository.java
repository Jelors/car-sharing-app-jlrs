package jlrs.carsharing.repository;

import java.util.List;
import java.util.Optional;
import jlrs.carsharing.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByRental_User_Id(Long userId);

    Optional<Payment> findBySessionId(String sessionId);
}
