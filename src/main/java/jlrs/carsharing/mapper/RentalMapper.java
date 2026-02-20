package jlrs.carsharing.mapper;

import java.time.LocalDate;
import jlrs.carsharing.config.MapperConfig;
import jlrs.carsharing.dto.rental.CreateRentalRequest;
import jlrs.carsharing.dto.rental.RentalResponse;
import jlrs.carsharing.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        config = MapperConfig.class,
        imports = LocalDate.class
)
public interface RentalMapper {
    @Mapping(target = "rentalDate", expression = "java(LocalDate.now())")
    @Mapping(target = "actualReturnDate", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "car", ignore = true)
    @Mapping(target = "user", ignore = true)
    Rental toModel(CreateRentalRequest createRentalRequest);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "car.id", target = "carId")
    RentalResponse toDto(Rental rental);
}
