package com.club_vibe.app_be.stripe.payments.controller;

import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.capture.CapturePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.refund.RefundPaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.capture.CapturePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.refund.RefundPaymentResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentProcessingController {

    /**
     * Authorize a payment (without capturing)
     * @param request {@link AuthorizePaymentRequest}
     * @return {@link AuthorizePaymentResponse}
     */
    @PostMapping("/authorize")
    public ResponseEntity<AuthorizePaymentResponse> authorizePayment(
            @Valid @RequestBody AuthorizePaymentRequest request
    ) {
        // TODO
        return ResponseEntity.ok(new AuthorizePaymentResponse(null, null));
    }

    /**
     * Capture a previously authorized payment and split funds
     * @param paymentId {@link Long}
     * @param request {@link CapturePaymentRequest}
     * @return {@link CapturePaymentResponse}
     */
    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<CapturePaymentResponse> capturePayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody CapturePaymentRequest request
    ) {
        // TODO
        return ResponseEntity.ok(new CapturePaymentResponse(null));
    }

    /**
     * Cancel an authorized payment (void)
     * @param paymentId {@link Long}
     * @return {@link Void}
     */
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<Void> cancelPaymentAuthorization(
            @PathVariable Long paymentId
    ) {
        // TODO
        return ResponseEntity.ok().build();
    }

    /**
     * Refund a captured payment
     * @param paymentId {@link Long}
     * @param request {@link RefundPaymentRequest}
     * @return {@link RefundPaymentResponse}
     */
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<RefundPaymentResponse> refundPayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody RefundPaymentRequest request
    ) {
        // TODO
        return ResponseEntity.ok(new RefundPaymentResponse(null));
    }

    /**
     * Webhook endpoint for Stripe payment events
     * @param payload {@link String}
     * @param sigHeader {@link String}
     * @return {@link Void}
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> handleStripePaymentWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        // TODO
        return ResponseEntity.ok().build();
    }
}
