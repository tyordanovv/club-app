package com.club_vibe.app_be.events.service.impl;

import com.club_vibe.app_be.common.enums.InvitationStatus;
import com.club_vibe.app_be.common.exception.InactiveEventException;
import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.events.EventMapper;
import com.club_vibe.app_be.events.dto.EventDTO;
import com.club_vibe.app_be.events.dto.create.CreateEventRequest;
import com.club_vibe.app_be.events.dto.create.CreateEventResponse;
import com.club_vibe.app_be.events.entity.EventConditionsEntity;
import com.club_vibe.app_be.events.entity.EventEntity;
import com.club_vibe.app_be.events.repository.EventConditionsRepository;
import com.club_vibe.app_be.events.repository.EventRepository;
import com.club_vibe.app_be.events.service.EventFactory;
import com.club_vibe.app_be.events.service.EventInvitationOrchestrator;
import com.club_vibe.app_be.events.service.EventService;
import com.club_vibe.app_be.events.dto.EventRequestsResponse;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.club.dto.ClubArtistPercentage;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import com.club_vibe.app_be.users.club.service.ClubService;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventInvitationOrchestrator invitationOrchestrator;
    private final EventRepository eventRepository;
    private final EntityManager entityManager;
    private final EventMapper eventMapper;
    private final EventConditionsRepository conditionsRepository;
    private final ClubService clubService;

    @Override
    public CreateEventResponse createEventAndInviteArtist(CreateEventRequest request, Long clubId) {
        EventEntity savedEvent = eventRepository.save(buildEvent(request, clubService.findClubArtistPercentage(clubId), clubId));
        triggerArtistInvitationAsync(savedEvent.getId(), clubId, request.artistId());
        return eventMapper.toCreateEventResponse(savedEvent, InvitationStatus.PENDING);
    }

    @Override
    public void activateEvent(Long id) {
        eventRepository.updateEventStatus(id, true);
    }

    @Override
    public EventDTO findEventById(Long id) throws ItemNotFoundException {
        return eventMapper.toEventDTO(
                eventRepository.findById(id)
                        .orElseThrow(() -> new ItemNotFoundException(NAME, id.toString()))
        );
    }

    @Override
    public EventDTO validateAndGetEvent(Long eventId) throws InactiveEventException {
        //TODO add validation if correct artist and club are connected
        EventDTO event = findEventById(eventId);
        if (!event.isActive()) throw new InactiveEventException(eventId);
        return event;
    }

    @Override
    public EventRequestsResponse getEventRequests(Long staffId) {
        Optional<EventEntity> event = findActiveEventByUserId(staffId);
        return event.map(eventEntity -> new EventRequestsResponse(
                eventEntity.getId(),
                eventMapper.toEventRequests(eventEntity)
        )).orElseGet(() -> new EventRequestsResponse(null, null));

    }

    private Optional<EventEntity> findActiveEventByUserId(Long staffId) {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findActiveEventByStaffId(staffId, now);
    }

    private EventEntity buildEvent(CreateEventRequest request, ClubArtistPercentage percentages, Long clubId) {
        EventConditionsEntity eventConditions = EventFactory.createConditions(request.conditions(), percentages);
        EventEntity event = new EventEntity();
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        event.setActive(false);
        event.setConditions(eventConditions);
        event.setClub(entityManager.find(ClubEntity.class, clubId)); // TODO Consider additional validations
        event.setArtist(entityManager.find(ArtistEntity.class, request.artistId())); // TODO Consider additional validations
        return event;
    }

    private void triggerArtistInvitationAsync(Long eventId, Long clubId, Long artistId) {
        Thread.startVirtualThread(() -> invitationOrchestrator.inviteArtistToEvent(eventId, clubId, artistId));
    }
}
