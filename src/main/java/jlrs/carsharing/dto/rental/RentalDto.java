package jlrs.carsharing.dto.rental;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RentalDto {
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;
    private Long carId;
    private Long userId;
}
