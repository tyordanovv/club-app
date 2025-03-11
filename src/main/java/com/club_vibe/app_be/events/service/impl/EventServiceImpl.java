package com.club_vibe.app_be.events.service.impl;

import com.club_vibe.app_be.common.enums.InvitationStatus;
import com.club_vibe.app_be.events.dto.EventDTO;
import com.club_vibe.app_be.events.dto.request.CreateEventRequest;
import com.club_vibe.app_be.events.entity.EventEntity;
import com.club_vibe.app_be.events.repository.EventRepository;
import com.club_vibe.app_be.events.service.EventInvitationOrchestrator;
import com.club_vibe.app_be.events.service.EventService;
import com.club_vibe.app_be.notification.service.InviteNotificationService;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.artist.service.impl.ArtistManagementServiceImpl;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventInvitationOrchestrator invitationOrchestrator;
    private final EventRepository eventRepository;
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public EventDTO createEventAndInviteArtist(CreateEventRequest request, Long clubId) {
        EventEntity event = new EventEntity();
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        event.setActive(false);
        event.setClub(entityManager.find(ClubEntity.class, clubId)); //TODO add check if clubId is valid
        event.setDj(entityManager.find(ArtistEntity.class, request.artistId()));//TODO add check if artistId is valid

        EventEntity savedEvent = eventRepository.save(event);

        handleInvitationAsynchronously(savedEvent.getId(), clubId, request.artistId());

        return savedEvent.mapToEventResponseDTOWithCustomStatus(InvitationStatus.PENDING);
    }

    @Override
    public void activateEvent(Long id) {
        eventRepository.updateEventStatus(id, true);
    }

    private void handleInvitationAsynchronously(Long eventId, Long clubId, Long artistId){
        Thread.startVirtualThread(() ->
                invitationOrchestrator.inviteArtistToEvent(eventId, clubId, artistId)
        );
    }
}
