package com.club_vibe.app_be.users.auth.service;

import com.club_vibe.app_be.users.auth.dto.StaffAuthenticationDTO;
import com.club_vibe.app_be.users.staff.role.StaffRole;

public interface JWTService {
    /**
     *
     * @param authenticationDto {@link StaffAuthenticationDTO}
     * @return String
     */
    String generateToken(StaffAuthenticationDTO authenticationDto);

    /**
     *
     * @param authenticationDto {@link StaffAuthenticationDTO}
     * @return {@link String}
     */
    String generateRefreshToken(StaffAuthenticationDTO authenticationDto);

    /**
     *
     * @param token {@link String}
     * @return {@link String}
     */
    String getEmail(String token);

    /**
     *
     * @param token {@link String}
     * @return {@link Long}
     */
    Long getUserId(String token);

    /**
     *
     * @param token {@link String}
     * @return {@link StaffRole}
     */
    StaffRole getUserRole(String token);

    /**
     *
     * @param token {@link String}
     * @return {@link Boolean}
     */
    boolean validateToken(String token);
}