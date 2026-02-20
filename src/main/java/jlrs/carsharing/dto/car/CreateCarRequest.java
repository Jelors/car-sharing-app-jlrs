package jlrs.carsharing.dto.car;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import jlrs.carsharing.model.Car;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCarRequest {
    @NotNull
    private String model;
    @NotNull
    private String brand;
    @NotNull
    private BigDecimal dailyFee;
    @NotNull
    private Car.Type type;
}
