package com.club_vibe.app_be.stripe.webhook.service.impl;

import com.club_vibe.app_be.stripe.accounts.service.ConnectedAccountService;
import com.club_vibe.app_be.stripe.payout.service.PayoutService;
import com.club_vibe.app_be.stripe.webhook.service.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Account;
import com.stripe.model.Event;
import com.stripe.model.Payout;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class StripeWebhookServiceImpl implements StripeWebhookService {

    private final ConnectedAccountService connectedAccountService;
    private final PayoutService payoutService;

    @Override
    public void handleEvent(Event event) throws SignatureVerificationException {
        log.info("Processing event: {}", event.getType());
        switch (event.getType()) {
            case EVENT_ACCOUNT_UPDATED:
                handleAccountUpdate(event);
                break;
            case EVENT_PAYMENT_SUCCESS:
                handlePaymentEvent(event);
                break;
            case EVENT_PAYOUT_CREATED:
            case EVENT_PAYOUT_SUCCESS:
            case EVENT_PAYOUT_FAILED:
            case EVENT_PAYOUT_CANCELED:
            case EVENT_PAYOUT_UPDATED:
                handlePayoutEvent(event);
                break;
            case EVENT_PAYMENT_FAILED:
            case EVENT_PAYMENT_CANCELED:
                handleFailedPayment(event);
            default:
                log.warn("Unhandled event type: {}", event.getType());
        }
    }

    /**
     * Handle account updates.
     * In this example, we check if the account is enabled, if KYC is complete (details submitted),
     * and if the account can both receive payments and make payouts.
     * @param event {@link Event}
     */
    private void handleAccountUpdate(Event event) throws SignatureVerificationException {
        Account account = (Account) event.getDataObjectDeserializer().getObject().orElseThrow();
        connectedAccountService.updateStripeAccount(account);
    }

    /**
     * Handle payment events.
     * For example, you might want to record successful payments.
     * @param event {@link Event}
     */
    private void handlePaymentEvent(Event event) {
        log.info("Processing payment event: {}", event.getType());
        // TODO: implement payment event handling
    }

    /**
     * Handle payout events.
     * Updates local payout records based on Stripe payout status changes.
     * @param event {@link Event}
     */
    private void handlePayoutEvent(Event event) {
        try {
            log.info("Processing payout event: {}", event.getType());

            Payout payout = (Payout) event.getDataObjectDeserializer().getObject().orElseThrow();

            String stripePayoutId = payout.getId();
            String status = payout.getStatus();

            log.info("Received payout update - ID: {}, Status: {}", stripePayoutId, status);

            updatePayoutInDatabase(stripePayoutId, status);

        } catch (Exception e) {
            log.error("Error processing payout event", e);
            // Don't rethrow as we don't want to fail the webhook response
            // Stripe will retry the webhook if we return an error
        }
    }

    /**
     * Updates the payout record in our database based on its Stripe ID.
     *
     * @param stripePayoutId The Stripe payout ID
     * @param stripeStatus The current status from Stripe
     */
    private void updatePayoutInDatabase(String stripePayoutId, String stripeStatus) {
        try {
            payoutService.updatePayoutStatusByStripeId(stripePayoutId, stripeStatus);
            log.info("Successfully updated payout status for stripe ID: {}", stripePayoutId);
        } catch (Exception e) {
            log.error("Failed to update payout status for stripe ID: {}", stripePayoutId, e);
        }
    }

    private void handleFailedPayment(Event event) {
        try {
            log.info("handleFailedPayment: {}", event);
        } catch (Exception e) {
        }
    }
}