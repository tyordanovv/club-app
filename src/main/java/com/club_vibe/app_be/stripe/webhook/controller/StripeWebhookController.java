package com.club_vibe.app_be.stripe.webhook.controller;

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
    private static final String ENDPOINT_SECRET = "";

    private final StripeWebhookService stripeWebhookService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(STRIPE_SIGNATURE_HEADER) String sigHeader) {

        try {
            log.info("Received payload: {}", payload);
            Event event = Webhook.constructEvent(payload, sigHeader, ENDPOINT_SECRET);
            log.info("Received event type: {}", event.getType());

            switch (event.getType()) {
                case "account.updated":
                    stripeWebhookService.handleAccountUpdate(event);
                    break;

                case "payment_intent.succeeded":
                    stripeWebhookService.handlePaymentEvent(event);
                    break;

                case "payout.paid":
                    stripeWebhookService.handlePayoutEvent(event);
                    break;

                default:
                    log.warn("Unhandled event type: {}", event.getType());
            }

            return ResponseEntity.ok("Webhook received");

        } catch (SignatureVerificationException e) {
            log.error("Invalid signature for webhook", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }
    }
}