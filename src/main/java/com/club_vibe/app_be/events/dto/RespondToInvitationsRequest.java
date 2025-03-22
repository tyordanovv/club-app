package com.club_vibe.app_be.events.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RespondToInvitationsRequest(
        @NotNull(message = "Invitation Id is required")
        @Min(value = 100, message = "Amount must be at least 100 cent")
        Long invitationId,
        @NotNull(message = "Invitation acceptance status is required")
        Boolean isAccepted,
        String responseMessage
) {
}
