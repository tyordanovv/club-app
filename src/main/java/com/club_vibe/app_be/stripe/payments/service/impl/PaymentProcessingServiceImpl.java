package com.club_vibe.app_be.stripe.payments.service.impl;

import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.capture.CapturePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.refund.RefundPaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.capture.CapturePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.refund.RefundPaymentResponse;
import com.club_vibe.app_be.stripe.payments.service.PaymentProcessingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class PaymentProcessingServiceImpl implements PaymentProcessingService {
    @Override
    public AuthorizePaymentResponse authorizePayment(AuthorizePaymentRequest request) {
        return null;
    }

    @Override
    public CapturePaymentResponse capturePayment(String paymentId, CapturePaymentRequest request) {
        return null;
    }

    @Override
    public void cancelPaymentAuthorization(String paymentId) {

    }

    @Override
    public RefundPaymentResponse refundPayment(String paymentId, RefundPaymentRequest request) {
        return null;
    }

    @Override
    public void handleStripePaymentWebhook(String payload, String sigHeader) {

    }
}
