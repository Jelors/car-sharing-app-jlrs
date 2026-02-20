package jlrs.carsharing.service.payment;

import static jlrs.carsharing.model.UserRole.RoleName;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jlrs.carsharing.dto.payment.CheckoutResponseDto;
import jlrs.carsharing.dto.payment.PaymentResponse;
import jlrs.carsharing.mapper.PaymentMapper;
import jlrs.carsharing.model.Payment;
import jlrs.carsharing.model.Rental;
import jlrs.carsharing.model.User;
import jlrs.carsharing.repository.PaymentRepository;
import jlrs.carsharing.repository.RentalRepository;
import jlrs.carsharing.service.PaymentService;
import jlrs.carsharing.service.RentalService;
import jlrs.carsharing.service.impl.user.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final RentalService rentalService;
    private final UserDetailsServiceImpl userDetailsService;
    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;

    private final String successUrl;
    private final String cancelUrl;
    private final String endpointSecret;

    public PaymentServiceImpl(
            @Value("${stripe.successUrl}") String successUrl,
            @Value("${stripe.cancelUrl}") String cancelUrl,
            @Value("${stripe.webhookSecret}") String endpointSecret,
            PaymentMapper paymentMapper,
            PaymentRepository paymentRepository,
            RentalRepository rentalRepository,
            RentalService rentalService,
            UserDetailsServiceImpl userDetailsService
    ) {
        this.cancelUrl = cancelUrl;
        this.endpointSecret = endpointSecret;
        this.paymentMapper = paymentMapper;
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
        this.rentalService = rentalService;
        this.successUrl = successUrl;
        this.userDetailsService = userDetailsService;
    }


    @Override
    public List<PaymentResponse> getAllPayments(Long userId) {
        User user = userDetailsService.getCurrentUser();
        if (user.getRoles().stream()
                .anyMatch(r -> r.getRole().equals(RoleName.MANAGER))) {
            return paymentRepository.findAllByRental_User_Id(userId)
                    .stream()
                    .map(paymentMapper::toDto)
                    .toList();
        }

        userId = user.getId();
        return paymentRepository.findAllByRental_User_Id(userId)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentResponse getPaymentBySessionId(String sessionId) throws IllegalAccessException {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Session with ID: {" + sessionId + "} not found!"
                ));
        User currentUser = userDetailsService.getCurrentUser();
        boolean isOwner = payment.getRental().getUser().getId().equals(currentUser.getId());
        boolean isManager = currentUser.getRoles().stream().anyMatch(r -> r.getRole().equals(RoleName.MANAGER));
        if (isOwner || isManager) {
            return paymentMapper.toDto(payment);
        }

        throw new IllegalAccessException("You don't have permissions to check "
                + "other users payments!");
    }

    @Override
    @Transactional
    public void handleWebhook(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("Invalid Stripe signature");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer()
                    .getObject()
                    .orElseThrow();

            markPaymentAsPaid(session.getId());
        }
    }

    @Override
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
                        .setSuccessUrl(successUrl)
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
                                                                        .setName("Car rental #" + rental.getId())
                                                                        .build()
                                                        ).build()
                                        ).build()
                        ).build();

        Session session = Session.create(params);

        createPendingPayment(rental, session, total);

        return new CheckoutResponseDto(session.getUrl());
    }

    @Transactional
    private void createPendingPayment(
            Rental rental,
            Session session,
            BigDecimal total
    ) {
        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setTotal(total);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        payment.setType(Payment.Type.PAYMENT);
        payment.setStatus(Payment.Status.PENDING);
        paymentRepository.save(payment);
    }

    @Transactional
    private void markPaymentAsPaid(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException(
                        "Payment not found for session: " + sessionId
                ));

        if (payment.getStatus() == Payment.Status.PAID) {
            throw new RuntimeException("Payment already PAID");
        }

        payment.setStatus(Payment.Status.PAID);

        Rental rental = payment.getRental();
        rental.setActive(false);

        paymentRepository.save(payment);
        rentalRepository.save(rental);
    }
}
