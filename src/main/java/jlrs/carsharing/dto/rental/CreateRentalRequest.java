package jlrs.carsharing.dto.rental;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRentalRequest {
    @NotNull
    private LocalDate returnDate;
    @NotNull
    private Long carId;
}
