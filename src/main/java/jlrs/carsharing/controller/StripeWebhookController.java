package jlrs.carsharing.controller;

import com.stripe.exception.SignatureVerificationException;
import jlrs.carsharing.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/webhooks")
public class StripeWebhookController {
    private final PaymentService paymentService;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) throws SignatureVerificationException {
        paymentService.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

}
