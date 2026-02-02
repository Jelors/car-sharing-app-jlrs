package jlrs.carsharing.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.util.List;
import jlrs.carsharing.dto.payment.CheckoutResponseDto;
import jlrs.carsharing.dto.payment.PaymentResponse;
import jlrs.carsharing.model.Rental;

public interface PaymentService {
    PaymentResponse createPendingPayment(Rental rental, Session session, BigDecimal total);

    CheckoutResponseDto createCheckout(Long rentalId) throws StripeException;

    List<PaymentResponse> getAllRentals(Long userId);

    PaymentResponse getPaymentBySessionId(String sessionId);

    void handleWebhook(String payload, String sigHeader) throws SignatureVerificationException;

    void markPaymentAsPaid(String sessionId);
}
