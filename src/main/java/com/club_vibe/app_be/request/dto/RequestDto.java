package com.club_vibe.app_be.request.dto;

import com.club_vibe.app_be.events.dto.RequestStatus;
import com.club_vibe.app_be.request.entity.RequestType;

/**
 *
 * @param requestId
 * @param type
 * @param title
 * @param message
 */
public record RequestDto(
        Long requestId,

        RequestType type,

        String title,

        String message,
        RequestStatus status
) { }
