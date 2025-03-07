package com.club_vibe.app_be.users.auth.dto;

import com.club_vibe.app_be.users.staff.role.StaffRole;

public record StaffAuthenticationDTO (Long id, String email, StaffRole role){}
