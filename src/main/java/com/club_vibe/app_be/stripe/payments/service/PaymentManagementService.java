package com.club_vibe.app_be.stripe.payments.service;

import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.stripe.exception.StripeException;

public interface PaymentManagementService {
    /**
     *
     * @param request
     * @param idempotencyKey
     * @return {@link AuthorizePaymentResponse}
     * @throws StripeException
     */
    AuthorizePaymentResponse authorizePayment(AuthorizePaymentRequest request, String idempotencyKey) throws StripeException;

    /**
     *
     * @param paymentIntentId
     * @param idempotencyKey
     */
    void captureAndSplitPayment(String paymentIntentId, String idempotencyKey) throws StripeException;
}
