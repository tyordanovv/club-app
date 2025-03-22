package com.club_vibe.app_be.integration.config;

import com.club_vibe.app_be.request.dto.reqest.InitializeRequest;
import com.club_vibe.app_be.request.entity.RequestType;
import com.club_vibe.app_be.stripe.payments.dto.PaymentSplitDetails;
import com.club_vibe.app_be.stripe.payments.dto.authorize.AuthorizePaymentRequest;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Test data for different payment scenarios
 */
public class TestPaymentData {
    // Test scenarios instead of specific card numbers
    public static final String TEST_CARD_SUCCESS = "success";
    public static final String TEST_CARD_REQUIRES_AUTH = "requires_auth";
    public static final String TEST_CARD_DECLINE = "decline";
    public static final String TEST_CARD_INSUFFICIENT_FUNDS = "insufficient_funds";

    // Test customer data
    public static final String TEST_CUSTOMER_EMAIL = "test@example.com";
    public static final String TEST_CUSTOMER_NAME = "Test Customer";

    // Test connected accounts (replace with your test connected accounts)
    public static final String TEST_CLUB_ACCOUNT_ID = "acct_1R15uSPSpR2zHjd3"; //Tihomir Yordanov
    public static final String TEST_ARTIST_ACCOUNT_ID = "acct_1R3aqGPLx9RH31Jt"; //Tihomir Yourdanov 2

    // Test event and request data
    public static final Long TEST_EVENT_ID = 123L;

    /**
     * Creates a test payment method in Stripe's test environment using test tokens
     */
    public static String createTestPaymentMethod(String cardNumber) throws StripeException {
        // Instead of using raw card numbers, use Stripe test tokens
        String tokenId;
        switch (cardNumber) {
            case TEST_CARD_SUCCESS:
                tokenId = "tok_visa"; // Token for a successful Visa card
                break;
            case TEST_CARD_REQUIRES_AUTH:
                tokenId = "tok_visa_3ds"; // Token for a card that requires 3DS authentication
                break;
            case TEST_CARD_DECLINE:
                tokenId = "tok_chargeDeclined"; // Token for a card that will be declined
                break;
            case TEST_CARD_INSUFFICIENT_FUNDS:
                tokenId = "tok_chargeDeclinedInsufficientFunds"; // Token for insufficient funds
                break;
            default:
                tokenId = "tok_visa"; // Default to success token
        }

        Map<String, Object> billingDetails = new HashMap<>();
        billingDetails.put("email", TEST_CUSTOMER_EMAIL);
        billingDetails.put("name", TEST_CUSTOMER_NAME);

        Map<String, Object> address = new HashMap<>();
        address.put("line1", "123 Test St");
        address.put("city", "Test City");
        address.put("state", "TS");
        address.put("postal_code", "12345");
        address.put("country", "US");
        billingDetails.put("address", address);

        Map<String, Object> params = new HashMap<>();
        params.put("type", "card");
        params.put("card", Map.of("token", tokenId));
        params.put("billing_details", billingDetails);

        PaymentMethod paymentMethod = PaymentMethod.create(params);
        return paymentMethod.getId();
    }
    /**
     * Creates a sample authorize payment request
     */
    public static AuthorizePaymentRequest createTestAuthorizeRequest(String paymentMethodId, Long eventId) {
        InitializeRequest initRequest = new InitializeRequest(
                RequestType.SONG,
                "Test Request Tile",
                "Test Request Description",
                TEST_CUSTOMER_EMAIL,
                eventId
        );

        return new AuthorizePaymentRequest(
                paymentMethodId,
                100.00,
                "BGN",
                TEST_CUSTOMER_EMAIL,
                initRequest
        );
    }

//    /**
//     * Creates sample payment split details
//     */
//    public static PaymentSplitDetails createTestSplitDetails() {
//        return new PaymentSplitDetails(
//                TEST_CLUB_ACCOUNT_ID,
//                TEST_ARTIST_ACCOUNT_ID,
//                70.0, // 70% to club
//                20.0  // 20% to artist, 10% to platform
//        );
//    }
}

