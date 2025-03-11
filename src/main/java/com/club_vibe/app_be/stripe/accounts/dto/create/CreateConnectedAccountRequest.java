package com.club_vibe.app_be.stripe.accounts.dto.create;

import com.club_vibe.app_be.common.enums.Country;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request for creating a connected account.
 * @param country
 * @param email
 */
public record CreateConnectedAccountRequest(
        Country country,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email
) {}