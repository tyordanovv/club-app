package com.club_vibe.app_be.users.auth.dto;

import com.club_vibe.app_be.common.enums.Country;
import com.club_vibe.app_be.users.staff.role.StaffRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Email is required")
        @Email
        String email,
        @NotBlank(message = "Password is required")
        String password,
        @NotBlank(message = "Name is required")
        String name,
        @NotNull(message = "Role is required")
        StaffRole role,
        @NotNull(message = "Country is required")
        Country country
) {}