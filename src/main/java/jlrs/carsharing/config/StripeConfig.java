package jlrs.carsharing.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class StripeConfig {

    @Value("${stripe.secretKey:sk_test_51StnthFHibKKZP0AaWVqIQklxPfPVuW2qvtld3R2aJYmo"
            + "KQM2kMeq7AArNKCsdBiNfvrX0mxjygPFiHvpHCPBNdA00tjksET73}")
    private String secretKey;

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("Stripe secretKey is not set!");
        }
        System.out.println("Stripe key loaded: OK");
        Stripe.apiKey = secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        Stripe.apiKey = secretKey;
    }
}
