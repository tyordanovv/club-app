package com.club_vibe.app_be.users.auth.dto;

import com.club_vibe.app_be.users.staff.role.StaffRole;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String email,
        StaffRole role,
        Long userId
) {}