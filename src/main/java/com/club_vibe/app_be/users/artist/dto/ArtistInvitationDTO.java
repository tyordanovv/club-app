package com.club_vibe.app_be.users.artist.dto;

import com.club_vibe.app_be.common.enums.InvitationStatus;

public record ArtistInvitationDTO (
        Long id,
        Long clubId,
        Long artistId,
        InvitationStatus status
) {
}
