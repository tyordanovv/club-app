package com.club_vibe.app_be.stripe.payments.controller;

import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.refund.RefundPaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.capture.CapturePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.refund.RefundPaymentResponse;
import com.club_vibe.app_be.stripe.payments.service.PaymentManagementService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.club_vibe.app_be.stripe.payments.controller.PaymentProcessingController.BASE_URL;

@RestController
@RequestMapping("/api/payments")
@AllArgsConstructor
@Slf4j
public class PaymentProcessingController {
    public static final String BASE_URL = "/api/payments";


    private final PaymentManagementService paymentManagementService;

    /**
     * Authorize a payment (without capturing)
     *
     * @param request {@link AuthorizePaymentRequest}
     * @param idempotencyKey {@link String}
     * @return {@link AuthorizePaymentResponse}
     * @throws StripeException
     */
    @PostMapping("/authorize")
    public ResponseEntity<AuthorizePaymentResponse> authorizePayment(
            @Valid @RequestBody AuthorizePaymentRequest request,
            @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey
    ) throws StripeException {
        log.info("Accepted payment auth request with Idempotency-Key = {} and Payment-Method-Id = {}.",
                idempotencyKey, request.paymentMethodId());
        return ResponseEntity.ok(paymentManagementService.authorizePayment(request, idempotencyKey));
    }

    /**
     * Artist captures a previously authorized payment and split funds between him, the club and the platform.
     *
     * @param paymentId {@link Long}
     * @param idempotencyKey
     * @return {@link CapturePaymentResponse}
     */
    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<Void> capturePayment(
            @PathVariable String paymentId,
            @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey
    ) throws StripeException {
        log.info("Accepted capture payment request with Idempotency-Key = {} and Payment-Method-Id = {}.",
                idempotencyKey, paymentId);
                paymentManagementService.captureAndSplitPayment(paymentId, idempotencyKey);
        return ResponseEntity.ok().build();
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
}
