package jlrs.carsharing.service.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import jlrs.carsharing.model.Payment;
import jlrs.carsharing.model.Rental;
import jlrs.carsharing.repository.PaymentRepository;
import jlrs.carsharing.repository.RentalRepository;
import jlrs.carsharing.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;

    @Override
    public PaymentIntent createPayment(Long rentalId) throws StripeException {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Rental with ID: {" + rentalId + "} not found!"));

        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setTotal(calculateTotal(rental));
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.PAYMENT);

        paymentRepository.save(payment);

        PaymentIntent intent = PaymentIntent.create(
                Map.of(
                        "amount", payment.getTotal()
                                .multiply(BigDecimal.valueOf(100))
                                .longValue(),
                        "currency", "usd",
                        "metadata", Map.of(
                                "paymentId", payment.getId().toString()
                        )
                )
        );

        payment.setSessionId(intent.getId());
        paymentRepository.save(payment);

        return intent;
    }

    private BigDecimal calculateTotal(Rental rental) {
        long days = ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
        if (days <= 1) {
            days = 1;
        }

        BigDecimal baseAmount = rental.getCar().getDailyFee().multiply(BigDecimal.valueOf(days));

        BigDecimal fines = BigDecimal.ZERO;
        if (rental.getActualReturnDate() != null &&
                rental.getActualReturnDate().isAfter(rental.getRentalDate())) {
            long lateDays = ChronoUnit.DAYS.between(rental.getReturnDate(), rental.getActualReturnDate());
            fines = rental.getCar().getDailyFee().multiply(BigDecimal.valueOf(lateDays)).multiply(BigDecimal.valueOf(1.4)); // 40% fine
        }

        return baseAmount.add(fines).setScale(2, RoundingMode.HALF_UP);
    }
}
