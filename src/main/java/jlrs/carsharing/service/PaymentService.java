package jlrs.carsharing.service;

import java.util.List;
import jlrs.carsharing.dto.payment.PaymentResponse;

public interface PaymentService {
    List<PaymentResponse> getAllPayments(Long userId);

    PaymentResponse getPaymentBySessionId(String sessionId) throws IllegalAccessException;
}
