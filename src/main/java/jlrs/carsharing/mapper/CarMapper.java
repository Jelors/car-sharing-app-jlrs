package jlrs.carsharing.mapper;

import jlrs.carsharing.config.MapperConfig;
import jlrs.carsharing.dto.car.CarDto;
import jlrs.carsharing.dto.car.CreateCarRequestDto;
import jlrs.carsharing.model.Car;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CarMapper {
    Car toModel(CreateCarRequestDto createRequest);

    CarDto toDto(Car car);
}
