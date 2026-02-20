package jlrs.carsharing.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    private final String secretKey;

    public StripeConfig(@Value("${stripe.secretKey}") String secretKey) {
        this.secretKey = secretKey;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = this.secretKey;
    }
}
