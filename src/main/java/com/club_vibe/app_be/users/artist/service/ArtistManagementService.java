package com.club_vibe.app_be.users.artist.service;

import com.club_vibe.app_be.users.artist.dto.InvitationArtistConfirmationRequest;

import java.util.List;

public interface ArtistManagementService {

    /**
     *
     * @param eventId
     * @param clubId
     * @param artistId
     */
    void inviteToEvent(Long eventId, Long clubId, Long artistId);

    /**
     *
     * @param requests List of {@link InvitationArtistConfirmationRequest}
     * @param artistId {@link Long}
     */
    void respondToInvitations(List<InvitationArtistConfirmationRequest> requests, Long artistId);
}
