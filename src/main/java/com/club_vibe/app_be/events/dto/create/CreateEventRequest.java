package com.club_vibe.app_be.events.dto.create;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @param startTime
 * @param endTime
 * @param artistId
 * @param artistPercentage
 * @param clubPercentage
 */
public record CreateEventRequest(
        @NotNull(message = "Start Time is required")
        LocalDateTime startTime,
        @NotNull(message = "End Time is required")
        LocalDateTime endTime,
        @NotNull(message = "Artist Id is required")
        Long artistId,
        @NotNull(message = "Artist percentage is required")
        BigDecimal artistPercentage,
        @NotNull(message = "Club percentage is required")
        BigDecimal clubPercentage
) {}
