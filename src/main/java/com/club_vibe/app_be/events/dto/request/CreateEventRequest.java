package com.club_vibe.app_be.events.dto.request;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateEventRequest(
        @NotNull(message = "Start Time is required")
        LocalDateTime startTime,
        @NotNull(message = "End Time is required")
        LocalDateTime endTime,
        @NotNull(message = "Artist Id is required")
        Long artistId
) {}
