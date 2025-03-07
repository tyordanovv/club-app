package com.club_vibe.app_be.events.service;

import com.club_vibe.app_be.events.dto.request.CreateEventRequest;
import com.club_vibe.app_be.events.dto.EventDTO;

public interface EventService {
    /**
     *
     * @param request {@link CreateEventRequest}
     * @param clubId {@link Long}
     * @return {@link EventDTO}
     */
    EventDTO createEventAndInviteArtist(CreateEventRequest request, Long clubId);

    /**
     *
     * @param id
     */
    void activateEvent(Long id);
}
