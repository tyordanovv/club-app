package com.club_vibe.app_be.users.artist.controller;

import com.club_vibe.app_be.users.artist.dto.InvitationArtistConfirmationRequest;
import com.club_vibe.app_be.users.artist.dto.PendingInvitationResponse;
import com.club_vibe.app_be.users.artist.service.ArtistInvitationService;
import com.club_vibe.app_be.users.artist.service.ArtistManagementService;
import com.club_vibe.app_be.users.auth.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/invitations")
public class ArtistInvitationController {
    private final CurrentUserService currentUserService;
    private final ArtistInvitationService artistInvitationService;
    private final ArtistManagementService artistManagementService;

    /**
     * Get pending invitations for the currently authenticated artist
     */
    @GetMapping
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<List<PendingInvitationResponse>> getPendingInvitations() {
        Long artistId = currentUserService.getCurrentUserPlatformId();
        List<PendingInvitationResponse> pendingInvitations = artistInvitationService.getPendingInvitationsForCurrentArtist(artistId);
        return ResponseEntity.ok(pendingInvitations);
    }

    /**
     * Endpoint for artist to respond to an event invitation
     *
     * @param requests response details (accept/decline)
     * @return updated event details
     */
    @PatchMapping
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<Void> respondToInvitation(
            @Valid @RequestBody List<InvitationArtistConfirmationRequest> requests
    ) {
        Long artistId = currentUserService.getCurrentUserPlatformId();
        artistManagementService.respondToInvitations(requests, artistId);
        return ResponseEntity.accepted().build();
    }
}
