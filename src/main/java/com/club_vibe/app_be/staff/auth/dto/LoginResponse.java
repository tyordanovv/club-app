package com.club_vibe.app_be.staff.auth.dto;

import com.club_vibe.app_be.staff.staff.role.StaffRole;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String email,
        StaffRole role,
        Long userId
) {}