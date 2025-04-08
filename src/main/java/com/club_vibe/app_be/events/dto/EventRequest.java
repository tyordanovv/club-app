package com.club_vibe.app_be.events.dto;

import com.club_vibe.app_be.request.dto.RequestDto;

public record EventRequest(
        String paymentIntentId,
        RequestDto request
) {
}
