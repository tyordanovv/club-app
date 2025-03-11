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
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void createNewArtistInvitation(Long eventId, Long clubId, Long artistId) {
        ArtistInvitationEntity invitation = new ArtistInvitationEntity();
        invitation.setId(eventId);
        invitation.setClub(entityManager.getReference(ClubEntity.class, clubId));
        invitation.setArtist(entityManager.getReference(ArtistEntity.class, artistId));
        invitation.setEventId(eventId);
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setCreatedAt(LocalDateTime.now());
        artistInvitationRepository.save(invitation);
    }

    @Override
    public ArtistInvitationDTO findById(Long invitationId) {
        return artistInvitationRepository.findById(invitationId)
                .map(artistMapper::mapArtistInvitationToDTO)
                .orElseThrow(() -> new ItemNotFoundException(invitationId.toString()));
    }

    @Override
    public List<PendingInvitationResponse> getPendingInvitationsForCurrentArtist(Long artistId) {
        InvitationStatus status = InvitationStatus.PENDING;
        log.info("Fetch {} Invitations for artist {}.", status.name(), artistId);

        return artistInvitationRepository.findPendingFutureInvitations(
                artistId, status.name(), LocalDateTime.now());
    }

    @Override
    public void updateInvitationStatus(InvitationArtistConfirmationRequest invitationArtistResponse) {
        InvitationStatus status = invitationArtistResponse.isAccepted()
                ? InvitationStatus.ACCEPTED : InvitationStatus.DECLINED;

        updateArtistInvitation(
                new UpdateArtistInvitation(
                        invitationArtistResponse.invitationId(),
                        status,
                        invitationArtistResponse.responseMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @Override
    public ArtistInvitationDTO validateArtistInvitation(Long invitationId, Long artistId) {
        ArtistInvitationDTO artistInvitation = this.findById(invitationId);

        if (!artistInvitation.artistId().equals(artistId)) {
            log.error("The invitation {} does not belong to user with identifier {}", invitationId, artistId);
            throw new AccessDeniedException("This invitation does not belong to you");
        }

        if (artistInvitation.status() != InvitationStatus.PENDING) {
            log.error("This invitation with identifier {} has already been responded to", invitationId);
            throw new IllegalStateException("This invitation has already been responded to");
        }

        log.info("Successful validation of invitation with identifier {}, which belongs to user with identifier {}",
                invitationId, artistId);
        return artistInvitation;
    }

    private ArtistInvitationEntity updateArtistInvitation(UpdateArtistInvitation updateArtistInvitationDTO) {
        ArtistInvitationEntity invitation = artistInvitationRepository.findById(updateArtistInvitationDTO.id())
                .orElseThrow(() -> new ItemNotFoundException(updateArtistInvitationDTO.id().toString()));

        invitation.setResponseMessage(updateArtistInvitationDTO.message());
        invitation.setStatus(updateArtistInvitationDTO.status());
        invitation.setRespondedAt(updateArtistInvitationDTO.time());

        return artistInvitationRepository.save(invitation);
    }
}
