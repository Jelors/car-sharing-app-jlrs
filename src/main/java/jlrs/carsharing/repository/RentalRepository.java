package jlrs.carsharing.repository;

import java.util.List;
import jlrs.carsharing.model.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("SELECT r FROM Rental r WHERE r.user.id = :userId AND r.isActive = :isActive")
    List<Rental> findAllByUserIdAndIsActive(@Param("userId") Long userId,
                                            @Param("isActive") boolean isActive);
}
