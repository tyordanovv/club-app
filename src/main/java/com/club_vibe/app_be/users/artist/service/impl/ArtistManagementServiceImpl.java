package com.club_vibe.app_be.users.artist.service.impl;

import com.club_vibe.app_be.events.service.EventInvitationOrchestrator;
import com.club_vibe.app_be.users.artist.dto.InvitationArtistConfirmationRequest;
import com.club_vibe.app_be.users.artist.service.ArtistManagementService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Service
@AllArgsConstructor
@Slf4j
public class ArtistManagementServiceImpl implements ArtistManagementService {
    private final EventInvitationOrchestrator invitationOrchestrator;

    @Override
    public void respondToInvitations(List<InvitationArtistConfirmationRequest> requests, Long artistId) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<? extends Future<?>> futures = requests.stream()
                    .map(request -> executor.submit(() -> invitationOrchestrator.processInvitationResponse(request, artistId)))
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
}
