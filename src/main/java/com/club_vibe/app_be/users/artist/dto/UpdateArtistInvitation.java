package com.club_vibe.app_be.users.artist.dto;

import com.club_vibe.app_be.common.enums.InvitationStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record UpdateArtistInvitation(
        @NotBlank Long id,
        @NotBlank InvitationStatus status,
        String message,
        @NotBlank LocalDateTime time
) {}
