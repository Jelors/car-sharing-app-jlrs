package jlrs.carsharing.dto.car;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import jlrs.carsharing.model.Car;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
public class CreateCarRequest {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    @NotNull
    private BigDecimal dailyFee;
    @NotNull
    private Car.Type type;
    @NotNull
    @Positive
    private Integer inventory;
}
