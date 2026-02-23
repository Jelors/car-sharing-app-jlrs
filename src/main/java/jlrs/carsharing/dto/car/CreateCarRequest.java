package jlrs.carsharing.dto.car;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotNull
    @Positive
    private Integer inventory;
}
