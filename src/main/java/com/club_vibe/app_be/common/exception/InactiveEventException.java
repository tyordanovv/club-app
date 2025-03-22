package com.club_vibe.app_be.common.exception;

public class InactiveEventException extends RuntimeException {
    public InactiveEventException(Long eventId) {
        super("Event with id " + eventId + " is not activated.");
    }
}
