package com.club_vibe.app_be.common.util.validator;

import java.math.BigDecimal;

public class PercentageValidator {

    private final static BigDecimal MAX_ARTIST_PERCENTAGE = BigDecimal.valueOf(90);
    private final static BigDecimal MAX_CLUB_PERCENTAGE = BigDecimal.valueOf(20);
    private final static BigDecimal MAX_COMBINED_PERCENTAGE = BigDecimal.valueOf(100);

    /**
     *
     * @param artistPercentage
     * @throws IllegalStateException
     */
    public static void validateArtistPercentage(BigDecimal artistPercentage) throws IllegalStateException {
        if (artistPercentage == null
                || (artistPercentage.signum() < 0)
                || (artistPercentage.compareTo(MAX_ARTIST_PERCENTAGE) > 0)) {
            throw new IllegalArgumentException("Invalid artist percentage: " + artistPercentage);
        }
    }

    /**
     *
     * @param clubPercentage
     * @throws IllegalStateException
     */
    public static void validateClubPercentage(BigDecimal clubPercentage) throws IllegalStateException {
        if (clubPercentage == null
                || (clubPercentage.signum() < 0)
                || (clubPercentage.compareTo(MAX_CLUB_PERCENTAGE) > 0)) {
            throw new IllegalArgumentException("Invalid club percentage: " + clubPercentage);
        }
    }

    /**
     *
     * @param clubPercentage
     * @param artistPercentage
     * @throws IllegalStateException
     */
    public static void validateTotalPercentage(BigDecimal clubPercentage, BigDecimal artistPercentage)
            throws IllegalStateException {
        BigDecimal totalPercentage = clubPercentage.add(artistPercentage);
        if (totalPercentage.compareTo(MAX_COMBINED_PERCENTAGE) >= 0) {
            throw new IllegalArgumentException("Combined split percentages exceed 100: " + totalPercentage);
        }
    }
}
