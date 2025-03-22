package com.club_vibe.app_be.unit.stripe;

import com.club_vibe.app_be.common.util.Amount;
import com.club_vibe.app_be.request.dto.reqest.InitializeRequest;
import com.club_vibe.app_be.request.entity.RequestType;
import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentRequest;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentResponse;
import com.club_vibe.app_be.stripe.payments.dto.capture.CapturePaymentResponse;
import com.club_vibe.app_be.stripe.payments.entity.StripePaymentStatus;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public abstract class AbstractPaymentTest {
    protected static final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(200.0);
    protected static final BigDecimal CAPTURED_AMOUNT = BigDecimal.valueOf(196.3);
    protected static final BigDecimal CLUB_PERCENTAGE = BigDecimal.valueOf(60); // 120
    protected static final BigDecimal ARTIST_PERCENTAGE = BigDecimal.valueOf(20); // 40
    protected static final Currency CURRENCY = Currency.getInstance("BGN");
    protected static final Long PAYMENT_ID = 123L;
    protected static final String PAYMENT_METHOD_ID = "pm_test_123";
    protected static final Double AMOUNT = 20.0;
    protected static final Currency CURRENCY_BGN = Currency.getInstance("BGN");
    protected static final String CURRENCY_BGN_STR = "BGN";
    protected static final String EMAIL = "user@test.com";
    protected static final String REQUEST_TITLE = "REQUEST_TITLE";
    protected static final String REQUEST_MESSAGE = "REQUEST_MESSAGE";
    protected static final Long REQUEST_EVENT_ID = 12345L;
    protected static final String PAYMENT_INTEND_ID = "pi_test_123";
    protected static final String CONN_ACC_CLUB = "CONN_ACC_CLUB";
    protected static final String CONN_ACC_ARTIST = "CONN_ACC_ARTIST";
    protected static final String TRANSFER_GROUP = "transferGroup_" + UUID.randomUUID();
    protected static final String IDEMPOTENCY_KEY = UUID.randomUUID().toString();


    protected CapturePaymentResponse createCapturePaymentResponse(Amount totalAmount, Integer capturedPercentage) {
        if (capturedPercentage >= 100) throw new IllegalArgumentException();
        return new CapturePaymentResponse(
                PAYMENT_INTEND_ID,
                totalAmount,
                totalAmount.calculatePercentage(BigDecimal.valueOf(capturedPercentage)),
                totalAmount.calculatePercentage(BigDecimal.valueOf(100 - capturedPercentage)),
                TRANSFER_GROUP
        );
    }

    protected PaymentSplitDetails createPaymentSplitDetails() {
        return new PaymentSplitDetails(
                PAYMENT_ID,
                CONN_ACC_CLUB,
                CONN_ACC_ARTIST,
                CLUB_PERCENTAGE,
                ARTIST_PERCENTAGE);
    }


    protected AuthorizePaymentRequest createAuthorizePaymentrequest() {
        return new AuthorizePaymentRequest(
                PAYMENT_METHOD_ID, AMOUNT, CURRENCY_BGN_STR, EMAIL,
                new InitializeRequest(
                        RequestType.SONG,
                        REQUEST_TITLE,
                        REQUEST_MESSAGE,
                        EMAIL,
                        REQUEST_EVENT_ID
                )

        );
    }

    protected AuthorizePaymentResponse createAuthorizePaymentResponse() {
        return new AuthorizePaymentResponse(
                PAYMENT_INTEND_ID,
                "secret_abc",
                false,
                StripePaymentStatus.AUTHENTICATED
        );
    }
}
