package jlrs.carsharing.mapper;

import jlrs.carsharing.config.MapperConfig;
import jlrs.carsharing.dto.payment.PaymentResponse;
import jlrs.carsharing.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(source = "rental.id", target = "rentalId")
    PaymentResponse toDto(Payment payment);

    @Mapping(target = "rental", ignore = true)
    Payment toModel(PaymentResponse paymentResponse);
}
