package jlrs.carsharing.dto.rental;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRentalRequest {
    private LocalDate returnDate;
    private Long carId;
}
