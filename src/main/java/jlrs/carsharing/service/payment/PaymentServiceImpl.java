package jlrs.carsharing.service.payment;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import jlrs.carsharing.dto.payment.PaymentResponse;
import jlrs.carsharing.mapper.PaymentMapper;
import jlrs.carsharing.model.Payment;
import jlrs.carsharing.model.Rental;
import jlrs.carsharing.repository.PaymentRepository;
import jlrs.carsharing.repository.RentalRepository;
import jlrs.carsharing.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    @Value("${stripe.webhook-secret}")
    private String endpointSecret;

    @Override
    public PaymentResponse createPayment(Long rentalId) throws StripeException {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Rental with ID: {" + rentalId + "} not found!"
                ));

        BigDecimal total = calculateTotal(rental);

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(cancelUrl)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("usd")
                                                        .setUnitAmount(total.multiply(BigDecimal.valueOf(100)).longValue())
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Car rental payment")
                                                                        .build()
                                                        ).build()
                                        )
                                        .build()
                        )
                        .build();

        Session session = Session.create(params);

        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        payment.setTotal(total);
        payment.setStatus(Payment.Status.PENDING);
        payment.setType(Payment.Type.PAYMENT);

        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public List<PaymentResponse> getAllRentals(Long userId) {
        return paymentRepository.findAllByRental_User_Id(userId)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentResponse getPaymentBySessionId(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Session with ID: {" + sessionId + "} not found!"
                ));
        return paymentMapper.toDto(payment);
    }

    @Override
    public String checkPaymentSuccess(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);

        PaymentResponse payment = getPaymentBySessionId(sessionId);

        if ("paid".equals(session.getPaymentStatus())) {
            payment.setStatus(Payment.Status.PAID);
            Payment paymentModel = paymentMapper.toModel(payment);
            paymentRepository.save(paymentModel);
            return "Payment success";
        }

        return "Payment failed.";
    }

    @Override
    @Transactional
    public void process(String payload, String sigHeader) throws SignatureVerificationException {
        Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event
                    .getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow();

            Payment payment = paymentRepository
                    .findBySessionId(session.getId())
                    .orElseThrow();

            if (payment.getStatus() != Payment.Status.PAID) {
                payment.setStatus(Payment.Status.PAID);
            }
        }
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
