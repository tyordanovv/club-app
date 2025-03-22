package com.club_vibe.app_be.stripe.payments.service;

import com.club_vibe.app_be.stripe.payments.dto.CreatePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.stripe.payments.entity.PaymentEntity;

public interface PaymentDataAccessService {
    String NAME = "Payment";

    /**
     *
     * @param request
     * @return
     */
    PaymentEntity createPayment(CreatePaymentRequest request);

    /**
     *
     * @param authResponse
     */
    void updatePaymentAfterAuthentication(PaymentEntity payment, AuthorizePaymentResponse authResponse);

    /**
     *
     * @param paymentIntentId
     * @return
     */
    PaymentSplitDetails getPaymentSplitData(String paymentIntentId);
}
