package com.club_vibe.app_be.common.util;

import java.time.LocalDateTime;

public class TimeUtils {
    public static LocalDateTime getFirstDayOfMonth() {
        return LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
    }
}
