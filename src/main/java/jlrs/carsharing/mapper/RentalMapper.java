package jlrs.carsharing.mapper;

import jlrs.carsharing.config.MapperConfig;
import jlrs.carsharing.dto.rental.CreateRentalRequestDto;
import jlrs.carsharing.dto.rental.RentalDto;
import jlrs.carsharing.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface RentalMapper {
    Rental toModel(CreateRentalRequestDto createRentalRequest);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "car.id", target = "carId")
    RentalDto toDto(Rental rental);
}
