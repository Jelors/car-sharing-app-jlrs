package jlrs.carsharing.dto.payment;

import java.math.BigDecimal;
import jlrs.carsharing.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private Long rentalId;
    private BigDecimal total;
    private Payment.Status status;
    private Payment.Type type;
}
