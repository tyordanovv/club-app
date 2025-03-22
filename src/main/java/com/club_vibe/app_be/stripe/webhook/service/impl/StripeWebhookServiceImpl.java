package com.club_vibe.app_be.stripe.webhook.service.impl;

import com.club_vibe.app_be.stripe.accounts.service.ConnectedAccountService;
import com.club_vibe.app_be.stripe.webhook.service.StripeWebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Account;
import com.stripe.model.Event;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class StripeWebhookServiceImpl implements StripeWebhookService {

    private final ConnectedAccountService connectedAccountService;

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
            case EVENT_PAYOUT_SUCCESS:
                handlePayoutEvent(event);
                break;
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
     * For example, record successful payouts or update account balances.
     * @param event {@link Event}
     */
    private void handlePayoutEvent(Event event) {
        log.info("Processing payout event: {}", event.getType());
        // TODO: implement payout event handling
    }
}