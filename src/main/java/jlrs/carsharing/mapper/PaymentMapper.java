package jlrs.carsharing.mapper;

import jlrs.carsharing.config.MapperConfig;
import jlrs.carsharing.dto.payment.CreatePendingPaymentRequestDto;
import jlrs.carsharing.dto.payment.PaymentResponse;
import jlrs.carsharing.model.Payment;
import jlrs.carsharing.repository.RentalRepository;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PaymentMapper {
    @Mapping(source = "rental.id", target = "rentalId")
    PaymentResponse toDto(Payment payment);

    @Mapping(target = "rental", ignore = true)
    Payment toModel(PaymentResponse paymentResponse);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rental", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Payment toModelFromCreateRequest(
            CreatePendingPaymentRequestDto requestDto,
            @Context RentalRepository rentalRepository
    );

    @AfterMapping
    default void setRental(
            @MappingTarget Payment payment,
            CreatePendingPaymentRequestDto requestDto,
            @Context RentalRepository rentalRepository
    ) {
        if (requestDto.rentalId() != null) {
            payment.setRental(rentalRepository.getReferenceById(requestDto.rentalId()));
        }
    }
}
