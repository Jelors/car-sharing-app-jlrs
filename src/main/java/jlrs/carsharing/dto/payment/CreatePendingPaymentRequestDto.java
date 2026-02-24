package jlrs.carsharing.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import org.springframework.validation.annotation.Validated;

@Validated
public record CreatePendingPaymentRequestDto(
        @NotNull Long rentalId,
        @NotBlank String sessionUrl,
        @NotBlank String sessionId,
        @Positive BigDecimal total
) {
}
