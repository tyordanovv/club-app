package com.club_vibe.app_be.unit.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.club_vibe.app_be.common.enums.InvitationStatus;
import com.club_vibe.app_be.events.EventMapper;
import com.club_vibe.app_be.events.dto.EventDTO;
import com.club_vibe.app_be.events.dto.create.CreateEventRequest;
import com.club_vibe.app_be.events.dto.create.CreateEventResponse;
import com.club_vibe.app_be.events.entity.EventEntity;
import com.club_vibe.app_be.events.repository.EventRepository;
import com.club_vibe.app_be.events.service.EventInvitationOrchestrator;
import com.club_vibe.app_be.events.service.impl.EventServiceImpl;
import com.club_vibe.app_be.users.artist.dto.ArtistDTO;
import com.club_vibe.app_be.users.artist.dto.ArtistDetails;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.club.dto.ClubDTO;
import com.club_vibe.app_be.users.club.dto.ClubDetails;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {

    @Mock
    private EventInvitationOrchestrator invitationOrchestrator;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private EventMapper eventMapper;

    private EventServiceImpl eventService;

    private static final BigDecimal ARTIST_AMOUNT = BigDecimal.valueOf(60);
    private static final BigDecimal CLUB_AMOUNT = BigDecimal.valueOf(20);
    @BeforeEach
    public void setup() {
        eventService = new EventServiceImpl(invitationOrchestrator, eventRepository, entityManager, eventMapper);
    }

    @Test
    void createEventAndInviteArtist_shouldSaveEventAndTriggerInvitation() {
        // Assume setters are available to set the required properties
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(2);
        CreateEventRequest request = new CreateEventRequest(startTime, endTime, 3L, ARTIST_AMOUNT, CLUB_AMOUNT);

        Long clubId = 2L;

        ClubEntity clubEntity = new ClubEntity();
        clubEntity.setId(clubId);
        ArtistEntity artistEntity = new ArtistEntity();
        artistEntity.setId(request.artistId());

        when(entityManager.find(ClubEntity.class, clubId)).thenReturn(clubEntity);
        when(entityManager.find(ArtistEntity.class, request.artistId())).thenReturn(artistEntity);

        EventEntity eventEntity = new EventEntity();
        eventEntity.setId(1L);
        eventEntity.setClub(clubEntity);
        eventEntity.setArtist(artistEntity);
        eventEntity.setClubPercentage(CLUB_AMOUNT);
        eventEntity.setArtistPercentage(ARTIST_AMOUNT);

        when(eventRepository.save(any(EventEntity.class))).thenReturn(eventEntity);
        when(eventMapper.toCreateEventResponse(eventEntity, InvitationStatus.PENDING))
                .thenReturn(new CreateEventResponse(
                        new EventDTO(1L, LocalDateTime.now(), LocalDateTime.now(), true,
                            new ClubDetails(1L, "CLUB_NAME",null, null),
                            new ArtistDetails(1L, "ARTIST_NAME", null, null)), InvitationStatus.PENDING));

        CreateEventResponse result = eventService.createEventAndInviteArtist(request, clubId);

        assertNotNull(result);
        verify(eventRepository).save(any(EventEntity.class));
        verify(eventMapper).toCreateEventResponse(any(EventEntity.class), any(InvitationStatus.class));

        // Allow a brief delay for the virtual thread to invoke the invitation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        verify(invitationOrchestrator).inviteArtistToEvent(eventEntity.getId(), clubId, request.artistId());
    }

    @Test
    void activateEvent_shouldUpdateEventStatus() {
        Long eventId = 1L;
        eventService.activateEvent(eventId);
        verify(eventRepository).updateEventStatus(eventId, true);
    }
}
