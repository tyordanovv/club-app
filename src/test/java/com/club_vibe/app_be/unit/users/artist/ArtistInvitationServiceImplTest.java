package com.club_vibe.app_be.unit.users.artist;

import com.club_vibe.app_be.common.enums.InvitationStatus;
import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.users.artist.dto.ArtistInvitationDTO;
import com.club_vibe.app_be.users.artist.dto.InvitationArtistConfirmationRequest;
import com.club_vibe.app_be.users.artist.dto.PendingInvitationResponse;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.artist.entity.ArtistInvitationEntity;
import com.club_vibe.app_be.users.artist.mapper.ArtistMapper;
import com.club_vibe.app_be.users.artist.repository.ArtistInvitationRepository;
import com.club_vibe.app_be.users.artist.service.impl.ArtistInvitationServiceImpl;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistInvitationServiceImplTest {

    @Mock
    private ArtistInvitationRepository artistInvitationRepository;

    @Mock
    private ArtistMapper artistMapper;

    @Mock
    private EntityManager entityManager;

    private ArtistInvitationServiceImpl invitationService;

    @BeforeEach
    public void setup() {
        invitationService = new ArtistInvitationServiceImpl(artistInvitationRepository, artistMapper, entityManager);
    }

    @Test
    void createNewArtistInvitation_shouldSaveInvitation() {
        Long eventId = 1L;
        Long clubId = 2L;
        Long artistId = 3L;

        ClubEntity clubEntity = new ClubEntity();
        ArtistEntity artistEntity = new ArtistEntity();

        when(entityManager.getReference(ClubEntity.class, clubId)).thenReturn(clubEntity);
        when(entityManager.getReference(ArtistEntity.class, artistId)).thenReturn(artistEntity);

        ArtistInvitationEntity invitationEntity = new ArtistInvitationEntity();
        when(artistInvitationRepository.save(any(ArtistInvitationEntity.class))).thenReturn(invitationEntity);

        invitationService.createNewArtistInvitation(eventId, clubId, artistId);

        verify(artistInvitationRepository).save(any(ArtistInvitationEntity.class));
    }

    @Test
    void findById_shouldReturnInvitationDTO_whenFound() {
        Long invitationId = 1L;
        ArtistInvitationEntity invitationEntity = new ArtistInvitationEntity();
        ArtistInvitationDTO invitationDTO = new ArtistInvitationDTO(invitationId, 20L, 20L, InvitationStatus.PENDING);

        when(artistInvitationRepository.findById(invitationId)).thenReturn(Optional.of(invitationEntity));
        when(artistMapper.mapArtistInvitationToDTO(invitationEntity)).thenReturn(invitationDTO);

        ArtistInvitationDTO result = invitationService.findById(invitationId);
        assertEquals(invitationDTO, result);
    }

    @Test
    void findById_shouldThrowException_whenNotFound() {
        Long invitationId = 1L;
        when(artistInvitationRepository.findById(invitationId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> invitationService.findById(invitationId));
    }

    @Test
    void getPendingInvitationsForCurrentArtist_shouldReturnList() {
        Long artistId = 1L;
        PendingInvitationResponse response = new PendingInvitationResponse(
                20L, 30L, LocalDateTime.now(), InvitationStatus.PENDING, "CLUB_NAME");
        List<PendingInvitationResponse> responses = List.of(response);

        when(artistInvitationRepository.findPendingFutureInvitations(eq(artistId), anyString(), any(LocalDateTime.class)))
                .thenReturn(responses);

        List<PendingInvitationResponse> result = invitationService.getPendingInvitationsForCurrentArtist(artistId);
        assertEquals(responses, result);
    }

    @Test
    void updateInvitationStatus_shouldUpdateInvitation() {
        InvitationArtistConfirmationRequest request = new InvitationArtistConfirmationRequest(1L, true, "Accepted");

        ArtistInvitationEntity invitationEntity = new ArtistInvitationEntity();
        invitationEntity.setId(1L);
        when(artistInvitationRepository.findById(1L)).thenReturn(Optional.of(invitationEntity));
        when(artistInvitationRepository.save(any(ArtistInvitationEntity.class))).thenReturn(invitationEntity);

        invitationService.updateInvitationStatus(request);

        verify(artistInvitationRepository).save(any(ArtistInvitationEntity.class));
    }

    @Test
    void validateArtistInvitation_shouldThrowAccessDenied_whenArtistIdMismatch() {
        Long invitationId = 1L;
        Long artistId = 10L;
        ArtistInvitationDTO invitationDTO = new ArtistInvitationDTO(1L, 20L, 20L, InvitationStatus.PENDING);

        when(artistInvitationRepository.findById(invitationId)).thenReturn(Optional.of(new ArtistInvitationEntity()));
        when(artistMapper.mapArtistInvitationToDTO(any(ArtistInvitationEntity.class))).thenReturn(invitationDTO);

        assertThrows(AccessDeniedException.class, () -> invitationService.validateArtistInvitation(invitationId, artistId));
    }

    @Test
    void validateArtistInvitation_shouldThrowIllegalStateException_whenInvitationNotPending() {
        Long invitationId = 1L;
        Long artistId = 10L;
        ArtistInvitationDTO invitationDTO = new ArtistInvitationDTO(invitationId, 20L, artistId, InvitationStatus.ACCEPTED);

        when(artistInvitationRepository.findById(invitationId)).thenReturn(Optional.of(new ArtistInvitationEntity()));
        when(artistMapper.mapArtistInvitationToDTO(any(ArtistInvitationEntity.class))).thenReturn(invitationDTO);

        assertThrows(IllegalStateException.class, () -> invitationService.validateArtistInvitation(invitationId, artistId));
    }

    @Test
    void validateArtistInvitation_shouldReturnInvitationDTO_whenValid() {
        Long invitationId = 1L;
        Long artistId = 10L;
        ArtistInvitationDTO invitationDTO = new ArtistInvitationDTO(invitationId, 20L, artistId, InvitationStatus.PENDING);

        when(artistInvitationRepository.findById(invitationId)).thenReturn(Optional.of(new ArtistInvitationEntity()));
        when(artistMapper.mapArtistInvitationToDTO(any(ArtistInvitationEntity.class))).thenReturn(invitationDTO);

        ArtistInvitationDTO result = invitationService.validateArtistInvitation(invitationId, artistId);
        assertEquals(invitationDTO, result);
    }
}