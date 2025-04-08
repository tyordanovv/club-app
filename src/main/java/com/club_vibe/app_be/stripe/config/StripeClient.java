package com.club_vibe.app_be.stripe.config;

import com.stripe.exception.StripeException;
import com.stripe.model.Balance;
import com.stripe.model.BalanceTransaction;
import com.stripe.model.BalanceTransactionCollection;
import com.stripe.model.Payout;
import com.stripe.net.RequestOptions;
import com.stripe.param.BalanceTransactionListParams;
import com.stripe.param.PayoutCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeClient {

    /**
     * Retrieves the current balance from Stripe
     * @param stripeAccountId The Stripe account ID
     * @return Balance object from Stripe
     * @throws StripeException if there's an issue with the Stripe API call
     */
    public Balance retrieveBalance(String stripeAccountId) throws StripeException {
        RequestOptions requestOptions = RequestOptions.builder()
                .setStripeAccount(stripeAccountId)
                .build();
        return Balance.retrieve(requestOptions);
    }

    /**
     * Lists balance transactions from Stripe
     * @param stripeAccountId The Stripe account ID
     * @param params Parameters for filtering transactions
     * @return Collection of balance transactions
     * @throws StripeException if there's an issue with the Stripe API call
     */
    public BalanceTransactionCollection listBalanceTransactions(
            String stripeAccountId,
            BalanceTransactionListParams params) throws StripeException {
        RequestOptions requestOptions = RequestOptions.builder()
                .setStripeAccount(stripeAccountId)
                .build();
        return BalanceTransaction.list(params, requestOptions);
    }

    /**
     * Create a payout for a connected account
     *
     * @param connectedAccountId The Stripe connected account ID
     * @param amountInCents The amount in cents
     * @param currency The currency code (e.g., "usd")
     * @param description The description for the payout
     * @return The created Stripe Payout
     * @throws StripeException If the Stripe API call fails
     */
    public Payout createPayout(String connectedAccountId, Long amountInCents, String currency, String description)
            throws StripeException {

        PayoutCreateParams payoutParams = PayoutCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency)
                .setDescription(description)
                .build();

        RequestOptions requestOptions = RequestOptions.builder()
                .setStripeAccount(connectedAccountId)
                .build();

        return Payout.create(payoutParams, requestOptions);
    }

    /**
     * Retrieve a payout by ID
     *
     * @param payoutId The Stripe payout ID
     * @return The Stripe Payout
     * @throws StripeException If the Stripe API call fails
     */
    public Payout retrievePayout(String payoutId) throws StripeException {
        return Payout.retrieve(payoutId);
    }
}
