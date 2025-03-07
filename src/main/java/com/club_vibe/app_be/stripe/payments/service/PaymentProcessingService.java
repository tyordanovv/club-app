package com.club_vibe.app_be.stripe.payments.service;

import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.capture.CapturePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.refund.RefundPaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.capture.CapturePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.refund.RefundPaymentResponse;

/**
 * Service for processing payments and managing payment-related operations.
 */
public interface PaymentProcessingService {

    /**
     * Authorizes a payment (without capturing).
     *
     * @param request The request containing payment details (e.g., amount, currency).
     * @return The response containing the PaymentIntent ID and client secret.
     */
    AuthorizePaymentResponse authorizePayment(AuthorizePaymentRequest request);

    /**
     * Captures a previously authorized payment and splits funds between accounts.
     *
     * @param paymentId The ID of the payment to capture.
     * @param request   The request containing split details.
     * @return The response containing the capture ID.
     */
    CapturePaymentResponse capturePayment(String paymentId, CapturePaymentRequest request);

    /**
     * Cancels an authorized payment (void).
     *
     * @param paymentId The ID of the payment to cancel.
     */
    void cancelPaymentAuthorization(String paymentId);

    /**
     * Refunds a captured payment.
     *
     * @param paymentId The ID of the payment to refund.
     * @param request   The request containing refund details.
     * @return The response containing the refund ID.
     */
    RefundPaymentResponse refundPayment(String paymentId, RefundPaymentRequest request);

    /**
     * Handles Stripe webhook events for payment updates.
     *
     * @param payload     The raw webhook payload.
     * @param sigHeader   The Stripe-Signature header for verification.
     */
    void handleStripePaymentWebhook(String payload, String sigHeader);
}