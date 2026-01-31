package jlrs.carsharing.service;

import com.stripe.exception.StripeException;
import java.util.List;
import jlrs.carsharing.dto.payment.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(Long rentalId) throws StripeException;

    List<PaymentResponse> getAllRentals(Long userId);

    PaymentResponse getPaymentBySessionId(String sessionId);

    String checkPaymentSuccess(String sessionId) throws StripeException;
}
