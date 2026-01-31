package jlrs.carsharing.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import java.util.List;
import jlrs.carsharing.dto.payment.PaymentResponse;
import jlrs.carsharing.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPayments(@RequestParam("user_id") Long userId) {
        return new ResponseEntity<>(
                paymentService.getAllRentals(userId),
                HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestParam Long rentalId)
            throws StripeException {
        return new ResponseEntity<>(
                paymentService.createPayment(rentalId),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(
            @RequestParam("session_id") String sessionId
    ) throws StripeException {
        return new ResponseEntity<>(
                paymentService.checkPaymentSuccess(sessionId),
                HttpStatus.OK
        );
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancelled() {
        return ResponseEntity.ok("Payment cancelled");
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) throws SignatureVerificationException {
        paymentService.process(payload, sigHeader);
        return ResponseEntity.ok().build();
    }
}
