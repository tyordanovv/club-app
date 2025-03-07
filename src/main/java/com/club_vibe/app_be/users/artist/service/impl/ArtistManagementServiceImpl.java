package com.club_vibe.app_be.users.artist.service.impl;

import com.club_vibe.app_be.events.service.EventService;
import com.club_vibe.app_be.notification.dto.ArtistNotificationRequest;
import com.club_vibe.app_be.notification.dto.ClubNotificationRequest;
import com.club_vibe.app_be.notification.service.impl.ArtistInviteNotificationService;
import com.club_vibe.app_be.notification.service.impl.ClubInviteNotificationService;
import com.club_vibe.app_be.users.artist.dto.ArtistInvitationDTO;
import com.club_vibe.app_be.users.artist.dto.InvitationArtistConfirmationRequest;
import com.club_vibe.app_be.users.artist.service.ArtistInvitationService;
import com.club_vibe.app_be.users.artist.service.ArtistManagementService;
import com.club_vibe.app_be.users.artist.service.ArtistService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Service
@AllArgsConstructor
@Slf4j
public class ArtistManagementServiceImpl implements ArtistManagementService {
    private final ArtistInvitationService artistInvitationService;
    private final ArtistService artistService;
    private final ClubInviteNotificationService clubInviteNotificationService;
    private final ArtistInviteNotificationService artistInviteNotificationService;
    private final EventService eventService;

    @Override
    public void inviteToEvent(Long eventId, Long clubId, Long artistId) {
        artistInvitationService.createNewArtistInvitation(eventId, clubId, artistId);

        artistInviteNotificationService.notify(
                new ArtistNotificationRequest(artistId) //TODO
        );
    }

    @Override
    public void respondToInvitations(List<InvitationArtistConfirmationRequest> requests, Long artistId) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<? extends Future<?>> futures = requests.stream()
                    .map(request -> executor.submit(() -> processInvitation(request, artistId)))
                    .toList();

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    log.error("Error processing invitation: {}", e.getMessage());
                }
            }
        }
    }

    @Transactional
    private void processInvitation(InvitationArtistConfirmationRequest request, Long artistId) {
        ArtistInvitationDTO invitation = artistInvitationService.validateArtistInvitation(request.invitationId(), artistId);

        boolean isAccepted = request.isAccepted();

        if (isAccepted) {
            eventService.activateEvent(request.invitationId());
        }

        clubInviteNotificationService.notify(
                new ClubNotificationRequest(
                        invitation.clubId(),
                        invitation.artistId(),
                        isAccepted,
                        request.responseMessage()
                )
        );

        artistInvitationService.updateInvitationStatus(request);
    }
}
