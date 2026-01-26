package jlrs.carsharing.service;

import java.util.List;
import jlrs.carsharing.dto.rental.CreateRentalRequestDto;
import jlrs.carsharing.dto.rental.RentalDto;

public interface RentalService {
    RentalDto addRental(CreateRentalRequestDto createRentalRequest);

    RentalDto addActualReturnDate(Long rentalId);

    RentalDto getRental(Long id);

    List<RentalDto> getRentalsByUserIdAndIsActive(Long id, boolean isActive);
}
