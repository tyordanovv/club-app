package com.club_vibe.app_be.stripe.payments.service.impl;

import com.club_vibe.app_be.common.util.Amount;
import com.club_vibe.app_be.events.dto.EventDTO;
import com.club_vibe.app_be.events.service.EventService;
import com.club_vibe.app_be.request.service.RequestService;
import com.club_vibe.app_be.stripe.payments.dto.CreatePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.authorize.StripePaymentAuthorizationRequest;
import com.club_vibe.app_be.stripe.payments.dto.capture.CapturePaymentResponse;
import com.club_vibe.app_be.stripe.payments.entity.PaymentEntity;
import com.club_vibe.app_be.stripe.payments.service.PartialPaymentService;
import com.club_vibe.app_be.stripe.payments.service.PaymentManagementService;
import com.club_vibe.app_be.stripe.payments.service.PaymentProcessingService;
import com.club_vibe.app_be.stripe.payments.service.PaymentDataAccessService;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentManagementServiceImpl implements PaymentManagementService {
    private final EventService eventService;
    private final RequestService requestService;
    private final PaymentProcessingService paymentProcessingService;
    private final PaymentDataAccessService paymentDataAccessService;
    private final PartialPaymentService partialPaymentService;
    @Override
    public AuthorizePaymentResponse authorizePayment(AuthorizePaymentRequest authRequest, String idempotencyKey) throws StripeException {
        Amount amount = Amount.fromAuthRequest(authRequest);
        EventDTO eventDetails = eventService.validateAndGetEvent(authRequest.request().eventId());
        Long requestId = requestService.initializeRequest(authRequest.request());
        PaymentEntity payment = paymentDataAccessService.createPayment(
                new CreatePaymentRequest(amount, requestId, Currency.getInstance(authRequest.currency())));

        AuthorizePaymentResponse authResponse = paymentProcessingService.authorizePayment(
                new StripePaymentAuthorizationRequest(
                        amount,
                        authRequest.paymentMethodId(),
                        "message 123",
                        "ORDER_" + UUID.randomUUID()
                ),
                idempotencyKey,
                eventDetails.artist().percentage(),
                eventDetails.artist().stripeDetails().getAccountId());

        paymentDataAccessService.updatePaymentAfterAuthentication(payment, authResponse);

        return authResponse;
    }

    @Override
    public void captureAndSplitPayment(String paymentIntentId, String idempotencyKey) throws StripeException {
        // Capture the payment via Stripe API.
        CapturePaymentResponse captureResponse = paymentProcessingService.capturePayment(paymentIntentId, idempotencyKey);
        log.info("Payment captured {}", captureResponse);

        // Retrieve split details using your existing method.
        PaymentSplitDetails splitData = paymentDataAccessService.getPaymentSplitData(paymentIntentId);
        log.info("Payment splitData for PaymentIntentId {}", splitData);

        // Record the partial payments in the database.
        partialPaymentService.recordAllocations(
                splitData, captureResponse.totalAmount(), captureResponse.capturedAmount());
        log.info("recorded allocations!");
    }
}
