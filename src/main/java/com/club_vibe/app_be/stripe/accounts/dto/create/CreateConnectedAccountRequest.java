package com.club_vibe.app_be.stripe.accounts.dto.create;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request for creating a connected account.
 */
public record CreateConnectedAccountRequest(
        @NotBlank(message = "Country code is required")
        @Size(min = 2, max = 2, message = "Country code must be 2 characters")
        String country, // Country code (e.g., "DE")

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email    // User's email
) {}