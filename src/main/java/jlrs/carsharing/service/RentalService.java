package jlrs.carsharing.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import jlrs.carsharing.dto.rental.CreateRentalRequest;
import jlrs.carsharing.dto.rental.RentalResponse;
import jlrs.carsharing.model.Rental;

public interface RentalService {
    RentalResponse addRental(CreateRentalRequest createRentalRequest);

    RentalResponse addActualReturnDate(Long rentalId) throws IllegalAccessException;

    RentalResponse getRental(Long id);

    List<RentalResponse> getRentalsByUserIdAndIsActive(Long id, Boolean isActive);

    List<RentalResponse> getOverdueRentalsByDate(LocalDate date);

    BigDecimal calculateTotal(Rental rental);
}
