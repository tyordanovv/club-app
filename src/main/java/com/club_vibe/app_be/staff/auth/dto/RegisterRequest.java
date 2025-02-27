package com.club_vibe.app_be.staff.auth.dto;

import com.club_vibe.app_be.staff.staff.role.StaffRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String name,
        @NotNull StaffRole role
) {}