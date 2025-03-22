package com.club_vibe.app_be.common.exception;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ErrorResponse {
    String code;
    String message;
    LocalDateTime timestamp;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
