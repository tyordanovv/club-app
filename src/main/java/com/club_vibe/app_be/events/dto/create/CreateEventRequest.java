package com.club_vibe.app_be.events.dto.create;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @param startTime
 * @param endTime
 * @param artistId
 */
public record CreateEventRequest(
        @NotNull(message = "Start Time is required")
        LocalDateTime startTime,
        @NotNull(message = "End Time is required")
        LocalDateTime endTime,
        @NotNull(message = "Artist Id is required")
        Long artistId,
        @NotNull(message = "Conditions are required")
        CreateEventConditionsRequest conditions
) {}
