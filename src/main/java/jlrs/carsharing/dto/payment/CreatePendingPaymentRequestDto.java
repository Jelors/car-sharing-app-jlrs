package jlrs.carsharing.dto.payment;

import java.math.BigDecimal;

public record CreatePendingPaymentRequestDto(
        Long rentalId,
        String sessionUrl,
        String sessionId,
        BigDecimal total
) {
}
