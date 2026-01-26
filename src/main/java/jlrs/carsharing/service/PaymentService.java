package jlrs.carsharing.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface PaymentService {
    PaymentIntent createPayment(Long rentalId) throws StripeException;
}
