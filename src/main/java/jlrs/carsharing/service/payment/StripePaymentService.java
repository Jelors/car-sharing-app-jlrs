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
import jlrs.carsharing.dto.payment.CheckoutResponseDto;
import jlrs.carsharing.dto.payment.CreatePendingPaymentRequestDto;
import jlrs.carsharing.dto.payment.PaymentResponse;
import jlrs.carsharing.mapper.PaymentMapper;
import jlrs.carsharing.model.Payment;
import jlrs.carsharing.model.Rental;
import jlrs.carsharing.repository.PaymentRepository;
import jlrs.carsharing.repository.RentalRepository;
import jlrs.carsharing.service.RentalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentService {
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final RentalService rentalService;
    private final PaymentMapper paymentMapper;

    private final String successUrl;
    private final String cancelUrl;
    private final String endpointSecret;

    public StripePaymentService(
            RentalRepository rentalRepository,
            PaymentRepository paymentRepository,
            RentalService rentalService,
            PaymentMapper paymentMapper,
            @Value("${stripe.successUrl}") String successUrl,
            @Value("${stripe.cancelUrl}") String cancelUrl,
            @Value("${stripe.webhookSecret}") String endpointSecret
    ) {
        this.rentalRepository = rentalRepository;
        this.paymentRepository = paymentRepository;
        this.rentalService = rentalService;
        this.paymentMapper = paymentMapper;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        this.endpointSecret = endpointSecret;
    }

    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);

            String eventType = event.getType();

            if ("checkout.session.completed".equals(eventType)) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();
                markPaymentAsPaid(session.getId());
            }
            else if ("checkout.session.expired".equals(eventType)) {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();
                updatePaymentStatus(session.getId(), Payment.Status.FAILED);
            }
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid Stripe signature");
        }
    }

    @Transactional
    public CheckoutResponseDto createCheckout(Long rentalId) throws StripeException {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Rental with ID: {" + rentalId + "} not found!"
                ));
        BigDecimal total = rentalService.calculateTotal(rental);

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(cancelUrl + "?session_id={CHECKOUT_SESSION_ID")
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("usd")
                                                        .setUnitAmount(total.multiply(BigDecimal.valueOf(100)).longValue())
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Car rental #" + rental.getId())
                                                                        .build()
                                                        ).build()
                                        ).build()
                        ).build();

        Session session = Session.create(params);

        CreatePendingPaymentRequestDto requestDto = new CreatePendingPaymentRequestDto(
                rental.getId(),
                session.getUrl(),
                session.getId(),
                total
        );

        PaymentResponse pendingPayment = createPendingPayment(requestDto);

        return new CheckoutResponseDto(pendingPayment.getSessionUrl());
    }

    @Transactional
    public void markPaymentAsPaid(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found for session: " + sessionId
                ));

        if (payment.getStatus() == Payment.Status.PAID) {
            return;
        }

        payment.setStatus(Payment.Status.PAID);

        Rental rental = payment.getRental();
        rental.setActive(false);

        paymentRepository.save(payment);
        rentalRepository.save(rental);
    }

    @Transactional
    public void updatePaymentStatus(String sessionId, Payment.Status status) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        if (payment.getStatus() == Payment.Status.PAID) {
            return;
        }

        payment.setStatus(status);
        paymentRepository.save(payment);
    }

    private PaymentResponse createPendingPayment(
            CreatePendingPaymentRequestDto requestDto
    ) {
        Payment payment = paymentMapper.toModelFromCreateRequest(requestDto, rentalRepository);
        paymentRepository.save(payment);
        return paymentMapper.toDto(payment);
    }

}
