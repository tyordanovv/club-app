package com.club_vibe.app_be.events.service;

import com.club_vibe.app_be.common.exception.InactiveEventException;
import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.events.dto.create.CreateEventRequest;
import com.club_vibe.app_be.events.dto.EventDTO;
import com.club_vibe.app_be.events.dto.create.CreateEventResponse;

public interface EventService {
    String NAME = "Event";

    /**
     *
     * @param request {@link CreateEventRequest}
     * @param clubId {@link Long} The identifier of the club.
     * @return {@link CreateEventResponse}
     */
    CreateEventResponse createEventAndInviteArtist(CreateEventRequest request, Long clubId);

    /**
     *
     * @param id {@link Long} The identifier of the event.
     */
    void activateEvent(Long id);

    /**
     * Fetches the event and additional information such as place and performing artist.
     *
     * @param id {@link Long} The identifier of the event.
     * @return {@link EventDTO} object containing detailed information about the event.
     * @throws ItemNotFoundException when the event does not exist.
     */
    EventDTO findEventById(Long id) throws ItemNotFoundException;

    /**
     * Fetches an event entity from the database and checks if it was activated. If the artist had accepted the
     * invitation the event will be set to true, otherwise the event will keep its initial status isActive = false.
     *
     * @param eventId {@link String} identifier of the {@link com.club_vibe.app_be.events.entity.EventEntity}.
     * @return {@link EventDTO} object containing detailed information about the event, if the event was activated.
     * @throws InactiveEventException is thrown, when the event was not activated.
     */
    EventDTO validateAndGetEvent(Long eventId) throws InactiveEventException;
}
