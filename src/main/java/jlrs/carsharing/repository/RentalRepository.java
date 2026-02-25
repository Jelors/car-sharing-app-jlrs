package jlrs.carsharing.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import jlrs.carsharing.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByUserIdAndIsActive(Long userId, boolean isActive);

    List<Rental> findAllByIsActive(boolean isActive);

    List<Rental> findAllByReturnDateLessThanEqualAndActualReturnDateIsNull(LocalDate date);

    Optional<Rental> findByUserIdAndId(Long userId, Long id);
}
