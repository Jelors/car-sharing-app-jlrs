package jlrs.carsharing.controller;

import com.stripe.exception.StripeException;
import jakarta.validation.constraints.Positive;
import java.util.List;
import jlrs.carsharing.dto.payment.CheckoutResponseDto;
import jlrs.carsharing.dto.payment.PaymentResponse;
import jlrs.carsharing.model.Payment;
import jlrs.carsharing.service.PaymentService;
import jlrs.carsharing.service.payment.StripePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final StripePaymentService stripePaymentService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public ResponseEntity<List<PaymentResponse>> getPayments(
            @RequestParam(value = "user_id", required = false) @Positive Long userId
    ) {
        return new ResponseEntity<>(
                paymentService.getAllPayments(userId),
                HttpStatus.OK
        );
    }

    @GetMapping("/session")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public ResponseEntity<PaymentResponse> getPaymentBySessionId(
            @RequestParam("session_id") String sessionId
    ) throws IllegalAccessException {
        return new ResponseEntity<>(
                paymentService.getPaymentBySessionId(sessionId),
                HttpStatus.OK
        );
    }

    @PostMapping("/checkout/{rentalId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'MANAGER')")
    public ResponseEntity<CheckoutResponseDto> createCheckout(
            @PathVariable @Positive Long rentalId
    ) throws StripeException {
        return new ResponseEntity<>(
                stripePaymentService.createCheckout(rentalId),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/success")
    public String handleSuccess(@RequestParam("session_id") String sessionId) {
        stripePaymentService.markPaymentAsPaid(sessionId);
        return "Payment successful! You can close this tab.";
    }

    @GetMapping("/cancel")
    public String handleCancel(@RequestParam("session_id") String sessionId) {
        stripePaymentService.updatePaymentStatus(sessionId, Payment.Status.CANCELED);
        return "Payment was canceled. You can try again.";
    }

}
