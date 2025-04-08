package com.club_vibe.app_be.events.dto;

import java.util.List;

public record EventRequestsResponse(
    Long eventId,
    List<EventRequest> requests
) {
}
