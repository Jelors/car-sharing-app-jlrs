package jlrs.carsharing.controller;

import jlrs.carsharing.service.payment.StripePaymentService;
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
    private final StripePaymentService stripePaymentService;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        stripePaymentService.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

}
