package jlrs.carsharing.service;

import java.math.BigDecimal;
import java.util.List;
import jlrs.carsharing.dto.rental.CreateRentalRequest;
import jlrs.carsharing.dto.rental.RentalResponse;
import jlrs.carsharing.model.Rental;

public interface RentalService {
    RentalResponse addRental(CreateRentalRequest createRentalRequest);

    RentalResponse addActualReturnDate(Long rentalId);

    RentalResponse getRental(Long id);

    List<RentalResponse> getRentalsByUserIdAndIsActive(Long id, boolean isActive);

    List<RentalResponse> getRentalsByActive(boolean isActive);

    BigDecimal calculateTotal(Rental rental);
}
