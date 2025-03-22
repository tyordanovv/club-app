package com.club_vibe.app_be.stripe.payments.service;

import com.club_vibe.app_be.common.util.Amount;
import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.authorize.StripePaymentAuthorizationRequest;
import com.club_vibe.app_be.stripe.payments.dto.capture.CapturePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.refund.RefundPaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.capture.CapturePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.refund.RefundPaymentResponse;
import com.stripe.exception.StripeException;

import java.math.BigDecimal;

/**
 * Service for processing payments and managing payment-related operations.
 */
public interface PaymentProcessingService {

    /**
     * Authorizes a payment (without capturing).
     * Create a {@link com.stripe.model.PaymentIntent} with a transfer group to be used later when splitting funds.
     *
     * @param request The request containing payment details (e.g., amount, currency).
     * @param idempotencyKey
     * @param artistPercentage
     * @param artistConnectedAccount
     * @return The response containing the PaymentIntent ID and client secret.
     */
    AuthorizePaymentResponse authorizePayment(
            StripePaymentAuthorizationRequest request,
            String idempotencyKey,
            BigDecimal artistPercentage,
            String artistConnectedAccount
    ) throws StripeException;

    /**
     * Captures a previously authorized payment and splits funds between accounts.
     *
     * @param paymentId The ID of the payment to capture.
     * @param idempotencyKey
     * @return The response containing the capture ID.
     */
    CapturePaymentResponse capturePayment(String paymentId, String idempotencyKey) throws StripeException;

    /**
     * Cancels an authorized payment (void).
     *
     * @param paymentId The ID of the payment to cancel.
     */
    void cancelPaymentAuthorization(String paymentId) throws StripeException;

    /**
     * Refunds a captured payment.
     *
     * @param paymentId The ID of the payment to refund.
     * @param request   The request containing refund details.
     * @return The response containing the refund ID.
     */
    RefundPaymentResponse refundPayment(String paymentId, RefundPaymentRequest request);

    /**
     * TODO make a DTO SplitPaymentsRequest
     * @param paymentIntentId
     * @param splitData
     * @param amount
     * @param idempotencyKey
     * @throws StripeException
     */
    void splitPayments(
            String paymentIntentId,
            PaymentSplitDetails splitData,
            Amount amount,
            String idempotencyKey
    ) throws StripeException;
}