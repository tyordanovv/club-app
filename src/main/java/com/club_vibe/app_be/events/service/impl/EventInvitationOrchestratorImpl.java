package com.club_vibe.app_be.events.service.impl;

import com.club_vibe.app_be.events.service.EventInvitationOrchestrator;
import com.club_vibe.app_be.notification.dto.ArtistNotificationRequest;
import com.club_vibe.app_be.notification.dto.ClubNotificationRequest;
import com.club_vibe.app_be.notification.service.impl.ArtistInviteNotificationService;
import com.club_vibe.app_be.notification.service.impl.ClubInviteNotificationService;
import com.club_vibe.app_be.rabbitmq.event.ActivationEvent;
import com.club_vibe.app_be.rabbitmq.producer.EventProducer;
import com.club_vibe.app_be.users.artist.dto.ArtistInvitationDTO;
import com.club_vibe.app_be.users.artist.dto.InvitationArtistConfirmationRequest;
import com.club_vibe.app_be.users.artist.service.ArtistInvitationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class EventInvitationOrchestratorImpl implements EventInvitationOrchestrator {

    private final ArtistInvitationService artistInvitationService;
    private final ArtistInviteNotificationService artistInviteNotificationService;
    private final ClubInviteNotificationService clubInviteNotificationService;
    private final EventProducer eventProducer;

    @Override
    public void inviteArtistToEvent(Long eventId, Long clubId, Long artistId) {
        createInvitation(eventId, clubId, artistId);
        notifyArtist(artistId);
    }

    @Transactional
    @Override
    public void processInvitationResponse(InvitationArtistConfirmationRequest request, Long artistId) {
        ArtistInvitationDTO invitation = artistInvitationService.validateArtistInvitation(request.invitationId(), artistId);
        if (request.isAccepted()) {
            publishActivationEvent(request.invitationId());
        }
        notifyClub(invitation, request);
        artistInvitationService.updateInvitationStatus(request);
    }

    private void createInvitation(Long eventId, Long clubId, Long artistId) {
        artistInvitationService.createNewArtistInvitation(eventId, clubId, artistId);
    }

    private void notifyArtist(Long artistId) {
        artistInviteNotificationService.notify(new ArtistNotificationRequest(artistId));
    }

    private void publishActivationEvent(Long invitationId) {
        ActivationEvent activationEvent = new ActivationEvent(invitationId);
        eventProducer.publishActivationEvent(activationEvent);
        log.info("Published activation event: {}", activationEvent);
    }

    private void notifyClub(ArtistInvitationDTO invitation, InvitationArtistConfirmationRequest request) {
        ClubNotificationRequest clubNotification = new ClubNotificationRequest(
                invitation.clubId(),
                invitation.artistId(),
                request.isAccepted(),
                request.responseMessage()
        );
        clubInviteNotificationService.notify(clubNotification);
    }
}
