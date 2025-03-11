package com.club_vibe.app_be.stripe.webhook.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Account;
import com.stripe.model.Event;

public interface StripeWebhookService {

    /**
     * Handle account updates.
     * In this example, we check if the account is enabled, if KYC is complete (details submitted),
     * and if the account can both receive payments and make payouts.
     * @param event {@link Event}
     */
    void handleAccountUpdate(Event event) throws SignatureVerificationException;

    /**
     * Handle payment events.
     * For example, you might want to record successful payments.
     * @param event {@link Event}
     */
    void handlePaymentEvent(Event event);

    /**
     * Handle payout events.
     * For example, record successful payouts or update account balances.
     * @param event {@link Event}
     */
    void handlePayoutEvent(Event event);
}
