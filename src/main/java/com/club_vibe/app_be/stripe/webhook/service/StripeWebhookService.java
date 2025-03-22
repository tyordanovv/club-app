package com.club_vibe.app_be.stripe.webhook.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Account;
import com.stripe.model.Event;

public interface StripeWebhookService {
    String EVENT_ACCOUNT_UPDATED = "account.updated";
    String EVENT_PAYMENT_SUCCESS = "payment_intent.succeeded";
    String EVENT_PAYOUT_SUCCESS = "payout.paid";
    /**
     *
     * @param event {@link Event}
     * @throws SignatureVerificationException
     */
    void handleEvent(Event event) throws SignatureVerificationException;
}
