package com.club_vibe.app_be.events.service;

import com.club_vibe.app_be.users.artist.dto.InvitationArtistConfirmationRequest;

public interface EventInvitationOrchestrator {
    /**
     * Invites an artist to an event by creating an invitation and sending a notification.
     * @param eventId
     * @param clubId
     * @param artistId
     */
    void inviteArtistToEvent(Long eventId, Long clubId, Long artistId);

    /**
     * Processes an artist's response to an invitation.
     * If accepted, it activates the event.
     * @param request
     * @param artistId
     */
    void processInvitationResponse(InvitationArtistConfirmationRequest request, Long artistId);
}
