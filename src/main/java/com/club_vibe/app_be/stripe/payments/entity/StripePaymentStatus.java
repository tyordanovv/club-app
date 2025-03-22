package com.club_vibe.app_be.stripe.payments.entity;

public enum StripePaymentStatus {
    CREATED,
    REQUIRES_AUTHENTICATION,
    AUTHENTICATED,
    PROCESSING,
    ARTIST_CANCELED,
    AUTO_CANCELED,
    FINISHED,
    ERROR,
    UNEXPECTED
}
