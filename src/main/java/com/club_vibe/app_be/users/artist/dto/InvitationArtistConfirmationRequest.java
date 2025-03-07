package com.club_vibe.app_be.users.artist.dto;

/**
 *
 * @param invitationId
 * @param isAccepted
 * @param responseMessage
 */
public record InvitationArtistConfirmationRequest(
        Long invitationId,
        boolean isAccepted,
        String responseMessage
) {}
