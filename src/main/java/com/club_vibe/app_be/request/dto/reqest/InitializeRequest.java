package com.club_vibe.app_be.request.dto.reqest;

import com.club_vibe.app_be.request.entity.RequestType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InitializeRequest (
        @NotNull(message = "Type of the request is required")
        RequestType type,

        String title,
        String message,

        @NotBlank(message = "User email is required")
        @Email
        String userEmail,

        @NotNull(message = "The event id is required")
        Long eventId
) {
}