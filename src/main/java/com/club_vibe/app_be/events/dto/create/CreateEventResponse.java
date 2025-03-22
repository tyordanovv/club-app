package com.club_vibe.app_be.events.dto.create;

import com.club_vibe.app_be.common.enums.InvitationStatus;
import com.club_vibe.app_be.events.dto.EventDTO;

public record CreateEventResponse(
        EventDTO event,
        InvitationStatus status
) {
}
