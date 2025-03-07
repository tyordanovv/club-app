package com.club_vibe.app_be.users.artist.service;

import com.club_vibe.app_be.users.artist.dto.ArtistInvitationDTO;
import com.club_vibe.app_be.users.artist.dto.InvitationArtistConfirmationRequest;
import com.club_vibe.app_be.users.artist.dto.PendingInvitationResponse;

import java.util.List;

public interface ArtistInvitationService {
    /**
     *
     * @param eventId
     * @param clubId
     * @param artistId
     */
    void createNewArtistInvitation(Long eventId, Long clubId, Long artistId);

    /**
     *
     * @param invitationId
     * @return
     */
    ArtistInvitationDTO findById(Long invitationId);

    /**
     *
     * @param artistId
     * @return
     */
    List<PendingInvitationResponse> getPendingInvitationsForCurrentArtist(Long artistId);

    /**
     *
     * @param invitationArtistResponse
     */
    void updateInvitationStatus(InvitationArtistConfirmationRequest invitationArtistResponse);

    /**
     *
     * @param invitationId
     * @param artistId
     * @return
     */
    ArtistInvitationDTO validateArtistInvitation(Long invitationId, Long artistId);
}
