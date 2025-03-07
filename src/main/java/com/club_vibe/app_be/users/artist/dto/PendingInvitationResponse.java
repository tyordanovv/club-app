package com.club_vibe.app_be.users.artist.dto;

import com.club_vibe.app_be.common.enums.InvitationStatus;

import java.time.LocalDateTime;

public record PendingInvitationResponse(
        Long invitationId,
        Long eventId,
        LocalDateTime eventStartTime,
        InvitationStatus status,
        String clubName
) {}