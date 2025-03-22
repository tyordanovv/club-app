package com.club_vibe.app_be.stripe.payments.service.impl;

import com.club_vibe.app_be.common.util.Amount;
import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;
import com.club_vibe.app_be.stripe.payments.dto.authorize.StripePaymentAuthorizationRequest;
import com.club_vibe.app_be.stripe.payments.dto.refund.RefundPaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.capture.CapturePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.refund.RefundPaymentResponse;
import com.club_vibe.app_be.stripe.payments.entity.StripePaymentStatus;
import com.club_vibe.app_be.stripe.payments.service.PaymentProcessingService;
import com.stripe.exception.StripeException;
import com.stripe.model.BalanceTransaction;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Transfer;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCaptureParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.TransferCreateParams;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;

@Service
@Slf4j
@AllArgsConstructor
public class PaymentProcessingServiceImpl implements PaymentProcessingService {
    @Override
    public AuthorizePaymentResponse authorizePayment(
            StripePaymentAuthorizationRequest request,
            String idempotencyKey,
            BigDecimal artistPercentage,
            String artistConnectedAccount
    ) {
        Amount artistAmount = request.amount().calculatePercentage(artistPercentage);

        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()// receipt_email
                .setAmount(request.amount().toCents())
                .setCurrency(request.amount().getCurrency())
                .setPaymentMethod(request.paymentMethodId())
                .setConfirm(true)
                .setDescription(request.message())
                .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                .setTransferGroup(request.orderGroup())
                .setReturnUrl("http://localhost:3011")
                .setTransferData(
                        PaymentIntentCreateParams.TransferData.builder()
                                .setAmount(artistAmount.toCents())
                                .setDestination(artistConnectedAccount)
                                .build()
                )
                .build();

        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params, requestOptions);

            // Determine if the payment requires additional 3DS authentication
            boolean requiresAction = "requires_action".equals(paymentIntent.getStatus());
            StripePaymentStatus status = requiresAction
                    ? StripePaymentStatus.REQUIRES_AUTHENTICATION
                    : StripePaymentStatus.AUTHENTICATED;

            return new AuthorizePaymentResponse(
                    paymentIntent.getId(),
                    paymentIntent.getClientSecret(),
                    requiresAction,
                    status
            );
        } catch (StripeException e) {
            log.error("Error authorizing payment via Stripe", e);
            return new AuthorizePaymentResponse(null, null, false, StripePaymentStatus.ERROR);
        }
    }

    @Override
    public CapturePaymentResponse capturePayment(String paymentIntentId, String idempotencyKey) throws StripeException {
        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();
        log.info("Retrieve payment with intend id {}.", paymentIntentId);
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

        if (!"requires_capture".equals(paymentIntent.getStatus())) {
            log.info("Payment is not in a capturable state. Current state: " + paymentIntent.getStatus());
            throw new RuntimeException("Payment is not in a capturable state. Current state: " + paymentIntent.getStatus());
        }

        PaymentIntentCaptureParams captureParams = PaymentIntentCaptureParams.builder().build();
        PaymentIntent capturedPaymentIntent = paymentIntent.capture(captureParams, requestOptions);

        log.info("Captured payment intent {}", capturedPaymentIntent);
        return buildCapturePaymentResponse(capturedPaymentIntent);
    }

    @Override
    public void cancelPaymentAuthorization(String paymentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentId);
        paymentIntent.cancel();
    }

    @Override
    public RefundPaymentResponse refundPayment(String paymentId, RefundPaymentRequest request) {
        return null;
    }

    @Override
    public void splitPayments(
            String paymentIntentId,
            PaymentSplitDetails splitData,
            Amount totalAmount,
            String idempotencyKey
    ) throws StripeException {
        log.info("Total captured amount: {}", totalAmount.toString());

        // Calculate split amounts using the percentages from the DTO
        Amount clubAmount = totalAmount.calculatePercentage(splitData.clubPercentage());
        Amount artistAmount = totalAmount.calculatePercentage(splitData.artistPercentage());
        long platformAmountInCents = totalAmount.toCents() - clubAmount.toCents() - artistAmount.toCents();
        log.info("Calculated split amounts: Club = {} cents, Artist = {} cents, Platform = {} cents",
                clubAmount.toCents(), artistAmount.toCents(), platformAmountInCents);

        // Create idempotency keys for each transfer
        String clubTransferIdempotencyKey = idempotencyKey + "_club";

        RequestOptions clubRequestOptions = RequestOptions.builder()
                .setIdempotencyKey(clubTransferIdempotencyKey)
                .build();

        try {
            // Create a transfer for the Club connected account
            TransferCreateParams clubTransferParams = TransferCreateParams.builder()
                    .setAmount(clubAmount.toCents())
                    .setCurrency(clubAmount.getCurrency())
                    .setDestination(splitData.clubConnectedAccountId())
                    .setTransferGroup(paymentIntentId)
                    .build();
            Transfer clubTransfer = Transfer.create(clubTransferParams, clubRequestOptions);
            log.info("Club transfer created with id: {}", clubTransfer.getId());
        } catch (Exception e) {
            log.info("Club transfer failed with err: {}", e.getMessage());
            throw e;
        }
    }

    private Amount getTransactionFeeAmount(PaymentIntent capturedPaymentIntent) {
        try {
            Charge charge = Charge.retrieve(capturedPaymentIntent.getLatestCharge());
            BalanceTransaction balanceTransaction = BalanceTransaction.retrieve(charge.getBalanceTransaction());
            long fee = balanceTransaction.getFee();
            return Amount.fromCents(fee, capturedPaymentIntent.getCurrency());
        } catch (StripeException e) {
            log.error("There was an error while extracting the transaction fee of payment {}.",
                    capturedPaymentIntent.getId());
            return Amount.of(0.0, capturedPaymentIntent.getCurrency());
        }
    }

    private CapturePaymentResponse buildCapturePaymentResponse(PaymentIntent capturedPaymentIntent) {
        Amount totalAmount = Amount.fromPaymentIntent(capturedPaymentIntent);
        Amount feeAmount = getTransactionFeeAmount(capturedPaymentIntent);
        BigDecimal capturedMoney = totalAmount.getValue().subtract(feeAmount.getValue());
        Currency currency = Currency.getInstance(capturedPaymentIntent.getCurrency().toUpperCase());
        Amount capturedAmount = Amount.of(capturedMoney, currency);

        String transferGroup = capturedPaymentIntent.getTransferGroup();

        return new CapturePaymentResponse(
                capturedPaymentIntent.getId(), totalAmount, capturedAmount, feeAmount, transferGroup);
    }
}
