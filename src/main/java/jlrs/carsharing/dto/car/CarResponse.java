package jlrs.carsharing.dto.car;

import java.math.BigDecimal;
import jlrs.carsharing.model.Car;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarResponse {
    private Long id;
    private String model;
    private String brand;
    private BigDecimal dailyFee;
    private int inventory;
    private Car.Type type;
}
