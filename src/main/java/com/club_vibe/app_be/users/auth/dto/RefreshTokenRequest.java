package com.club_vibe.app_be.users.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank String refreshToken
) {}