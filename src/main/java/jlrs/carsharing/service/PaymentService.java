package jlrs.carsharing.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import java.util.List;
import jlrs.carsharing.dto.payment.CheckoutResponseDto;
import jlrs.carsharing.dto.payment.PaymentResponse;

public interface PaymentService {
    CheckoutResponseDto createCheckout(Long rentalId) throws StripeException;

    List<PaymentResponse> getAllPayments(Long userId);

    PaymentResponse getPaymentBySessionId(String sessionId) throws IllegalAccessException;

    void handleWebhook(String payload, String sigHeader) throws SignatureVerificationException;
}
