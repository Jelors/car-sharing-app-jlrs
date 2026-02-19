package jlrs.carsharing.mapper;

import jlrs.carsharing.config.MapperConfig;
import jlrs.carsharing.dto.car.CarResponse;
import jlrs.carsharing.dto.car.CreateCarRequest;
import jlrs.carsharing.model.Car;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CarMapper {
    Car toModel(CreateCarRequest createRequest);

    CarResponse toDto(Car car);
}
