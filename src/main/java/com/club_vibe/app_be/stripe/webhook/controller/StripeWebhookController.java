package com.club_vibe.app_be.stripe.webhook.controller;

import com.club_vibe.app_be.stripe.config.StripeConfig;
import com.club_vibe.app_be.stripe.webhook.service.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Account;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/webhook/stripe")
@Slf4j
@AllArgsConstructor
public class StripeWebhookController {

    private static final String STRIPE_SIGNATURE_HEADER = "Stripe-Signature";

    private final StripeWebhookService stripeWebhookService;
    private final StripeConfig stripeConfig;

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(STRIPE_SIGNATURE_HEADER) String sigHeader
    ) {
        try {
            log.info("Received payload: {}", payload);
            Event event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getEndpointSecret());
            log.info("Received event type: {}", event.getType());

            stripeWebhookService.handleEvent(event);

            return ResponseEntity.ok().build();
        } catch (SignatureVerificationException e) {
            log.error("Invalid signature for webhook", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }
    }
}