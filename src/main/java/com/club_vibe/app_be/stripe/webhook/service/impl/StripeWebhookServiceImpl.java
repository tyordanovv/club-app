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
    public void handleAccountUpdate(Event event) throws SignatureVerificationException {
        Account account = (Account) event.getDataObjectDeserializer().getObject().orElseThrow();
        connectedAccountService.updateStripeAccount(account);
    }

    @Override
    public void handlePaymentEvent(Event event) {
        log.info("Processing payment event: {}", event.getType());
        // TODO
    }

    @Override
    public void handlePayoutEvent(Event event) {
        log.info("Processing payout event: {}", event.getType());
        // TODO
    }
}
