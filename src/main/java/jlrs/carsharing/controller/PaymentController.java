package jlrs.carsharing.controller;

import com.stripe.exception.StripeException;
import java.util.List;
import jlrs.carsharing.dto.payment.CheckoutResponseDto;
import jlrs.carsharing.dto.payment.PaymentResponse;
import jlrs.carsharing.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPayments(
            @RequestParam("user_id") Long userId
    ) {
        return new ResponseEntity<>(
                paymentService.getAllRentals(userId),
                HttpStatus.OK
        );
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<PaymentResponse> getPaymentBySessionId(
            @RequestParam("session_id") String sessionId
    ) {
        return new ResponseEntity<>(
                paymentService.getPaymentBySessionId(sessionId),
                HttpStatus.OK
        );
    }

    @PostMapping("/checkout/{rentalId}")
    public ResponseEntity<CheckoutResponseDto> createCheckout(
            @PathVariable Long rentalId
    ) throws StripeException {
        return new ResponseEntity<>(
                paymentService.createCheckout(rentalId),
                HttpStatus.CREATED
        );
    }
}
