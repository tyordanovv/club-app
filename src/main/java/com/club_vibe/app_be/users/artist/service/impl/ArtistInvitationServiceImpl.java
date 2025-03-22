package com.club_vibe.app_be.users.artist.service.impl;

import com.club_vibe.app_be.common.enums.InvitationStatus;
import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.users.artist.dto.ArtistInvitationDTO;
import com.club_vibe.app_be.users.artist.dto.InvitationArtistConfirmationRequest;
import com.club_vibe.app_be.users.artist.dto.PendingInvitationResponse;
import com.club_vibe.app_be.users.artist.dto.UpdateArtistInvitation;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.artist.entity.ArtistInvitationEntity;
import com.club_vibe.app_be.users.artist.mapper.ArtistMapper;
import com.club_vibe.app_be.users.artist.repository.ArtistInvitationRepository;
import com.club_vibe.app_be.users.artist.service.ArtistInvitationService;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ArtistInvitationServiceImpl implements ArtistInvitationService {

    private final ArtistInvitationRepository artistInvitationRepository;
    private final ArtistMapper artistMapper;
    private final EntityManager entityManager;

    @Override
    public void createNewArtistInvitation(Long eventId, Long clubId, Long artistId) {
        ArtistInvitationEntity invitation = new ArtistInvitationEntity();
        invitation.setEventId(eventId);
        invitation.setClub(entityManager.getReference(ClubEntity.class, clubId));
        invitation.setArtist(entityManager.getReference(ArtistEntity.class, artistId));
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setCreatedAt(LocalDateTime.now());
        artistInvitationRepository.save(invitation);
    }

    @Override
    public ArtistInvitationDTO findById(Long invitationId) {
        return artistInvitationRepository.findById(invitationId)
                .map(artistMapper::mapArtistInvitationToDTO)
                .orElseThrow(() -> new ItemNotFoundException(NAME, invitationId.toString()));
    }

    @Override
    public List<PendingInvitationResponse> getPendingInvitationsForCurrentArtist(Long artistId) {
        InvitationStatus status = InvitationStatus.PENDING;
        log.info("Fetching {} invitations for artist {}.", status, artistId);
        return artistInvitationRepository.findPendingFutureInvitations(artistId, status.name(), LocalDateTime.now());
    }

    @Override
    public void updateInvitationStatus(InvitationArtistConfirmationRequest invitationArtistResponse) {
        InvitationStatus status = invitationArtistResponse.isAccepted()
                ? InvitationStatus.ACCEPTED : InvitationStatus.DECLINED;
        UpdateArtistInvitation updateDto = new UpdateArtistInvitation(
                invitationArtistResponse.invitationId(),
                status,
                invitationArtistResponse.responseMessage(),
                LocalDateTime.now()
        );
        updateArtistInvitation(updateDto);
    }

    @Override
    public ArtistInvitationDTO validateArtistInvitation(Long invitationId, Long artistId) {
        ArtistInvitationDTO invitation = findById(invitationId);
        if (!invitation.artistId().equals(artistId)) {
            log.error("Invitation {} does not belong to artist {}", invitationId, artistId);
            throw new AccessDeniedException("This invitation does not belong to you");
        }
        if (invitation.status() != InvitationStatus.PENDING) {
            log.error("Invitation {} has already been responded to", invitationId);
            throw new IllegalStateException("This invitation has already been responded to");
        }
        log.info("Validated invitation {} for artist {}", invitationId, artistId);
        return invitation;
    }

    private ArtistInvitationEntity updateArtistInvitation(UpdateArtistInvitation updateDto) {
        ArtistInvitationEntity invitation = artistInvitationRepository.findById(updateDto.id())
                .orElseThrow(() -> new ItemNotFoundException(NAME, updateDto.id().toString()));
        invitation.setResponseMessage(updateDto.message());
        invitation.setStatus(updateDto.status());
        invitation.setRespondedAt(updateDto.time());
        return artistInvitationRepository.save(invitation);
    }
}
