package com.club_vibe.app_be.stripe.webhook.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Account;
import com.stripe.model.Event;

public interface StripeWebhookService {
    /* Connected account events from Stripe */
    String EVENT_ACCOUNT_UPDATED = "account.updated";

    /* Payments events from Stripe */
    String EVENT_PAYMENT_SUCCESS = "payment_intent.succeeded";
    String EVENT_PAYMENT_FAILED = "payment_intent.processing";
    String EVENT_PAYMENT_CANCELED = "payment_intent.canceled";

    /* Payout events from Stripe */
    String EVENT_PAYOUT_SUCCESS = "payout.paid";
    String EVENT_PAYOUT_CREATED = "payout.created";
    String EVENT_PAYOUT_FAILED = "payout.failed";
    String EVENT_PAYOUT_CANCELED = "payout.canceled";
    String EVENT_PAYOUT_UPDATED = "payout.updated";

    /**
     *
     * @param event {@link Event}
     * @throws SignatureVerificationException
     */
    void handleEvent(Event event) throws SignatureVerificationException;
}
