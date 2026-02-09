package jlrs.carsharing.repository;

import java.util.List;
import jlrs.carsharing.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findAllByUserIdAndActive(Long userId, boolean active);

    List<Rental> findAllByActive(boolean active);
}
