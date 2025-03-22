package com.club_vibe.app_be.stripe.balance.servce;

import com.stripe.exception.StripeException;
import com.stripe.model.Balance;
import com.stripe.model.BalanceTransaction;
import com.stripe.model.BalanceTransactionCollection;
import com.stripe.net.RequestOptions;
import com.stripe.param.BalanceTransactionListParams;
import org.springframework.stereotype.Service;

@Service
public class StripeBalanceClient {

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
}
