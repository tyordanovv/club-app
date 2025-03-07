package com.club_vibe.app_be.notification.dto;

public record ClubNotificationRequest(
        Long clubId,
        Long artistId,
        boolean hadAccepted,
        String message
) {}
