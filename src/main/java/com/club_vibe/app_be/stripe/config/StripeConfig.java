package com.club_vibe.app_be.stripe.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeConfig {
    @Value("${stripe.secret-key}")
    private String stripeSecretKey;

    @Getter
    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

}
