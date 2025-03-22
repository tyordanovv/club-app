package com.club_vibe.app_be.stripe.payments.dto.capture;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

/**
 * Request for capturing a payment and splitting funds.
 */
public record CapturePaymentRequest(

//        @NotBlank(message = "Main account ID is required")
//        String mainAccountId,
//
//        @NotNull(message = "Connected account IDs are required")
//        @Size(min = 1, message = "At least one connected account ID is required")
//        List<String> connectedAccountIds,   // IDs of connected accounts
//
//        @NotNull(message = "Split percentages are required")
//        Map<String, Integer> splitPercentages
) {}