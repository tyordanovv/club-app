package com.club_vibe.app_be.common.util;

import java.math.BigDecimal;

public class DefaultPlatformValues {
    public static final String PLATFORM_CURRENCY = "BGN";
    public static final int DEFAULT_MAX_REQUESTS = 15;
    public static final long PLATFORM_COMMISSION = 15L;
    public static final BigDecimal ARTIST_COMMISSION = BigDecimal.valueOf(75L);
    public static final BigDecimal CLUB_COMMISSION = BigDecimal.valueOf(10L);
    public static final long PLATFORM_PAYED_AMOUNT = 100L - PLATFORM_COMMISSION;
    public static final BigDecimal MIN_PRICE_SONG_REQUESTS = BigDecimal.valueOf(20L);
    public static final BigDecimal MIN_PRICE_GREETING_REQUESTS = BigDecimal.valueOf(30L);
    public static final BigDecimal MIN_PRICE_PICTURE_REQUESTS = BigDecimal.valueOf(100L);
}
