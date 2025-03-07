package com.club_vibe.app_be.users.auth.service.impl;

import com.club_vibe.app_be.common.exception.InvalidTokenException;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.auth.dto.*;
import com.club_vibe.app_be.users.auth.service.AuthService;
import com.club_vibe.app_be.users.auth.service.JWTService;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import com.club_vibe.app_be.users.staff.entity.StaffEntity;
import com.club_vibe.app_be.users.staff.role.StaffRole;
import com.club_vibe.app_be.users.staff.service.StaffService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final StaffService staffService; // Using interface for easier testing and decoupling
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService; // Prefer interface for flexibility
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse register(RegisterRequest request) {
        StaffEntity newUser = switch (request.role()) {
            case CLUB -> ClubEntity.builder()
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .name(request.name())
                    .role(StaffRole.CLUB)
                    .build();
            case ARTIST -> ArtistEntity.builder()
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .stageName(request.name())
                    .role(StaffRole.ARTIST)
                    .build();
        };

        return generateAuthResponse(staffService.saveAndReturnDTO(newUser));
    }

    @Override
    public LoginResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );
        return generateAuthResponse(staffService.findStaffAuthByEmail(request.email()));
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        if (!jwtService.validateToken(request.refreshToken())) {
            throw new InvalidTokenException("Refresh token is invalid");
        }

        String userEmail = jwtService.getEmail(request.refreshToken());
        StaffAuthenticationDTO user = staffService.findStaffAuthByEmail(userEmail);

        return new LoginResponse(
                jwtService.generateToken(user),
                request.refreshToken(),
                user.email(),
                user.role(),
                user.id()
        );
    }

    private LoginResponse generateAuthResponse(StaffAuthenticationDTO user) {
        return new LoginResponse(
                jwtService.generateToken(user),
                jwtService.generateRefreshToken(user),
                user.email(),
                user.role(),
                user.id()
        );
    }
}
