package com.club_vibe.app_be.staff.auth.service;

import com.club_vibe.app_be.common.exception.InvalidTokenException;
import com.club_vibe.app_be.common.exception.UserNotFoundException;
import com.club_vibe.app_be.staff.artist.entity.ArtistEntity;
import com.club_vibe.app_be.staff.auth.dto.AuthRequest;
import com.club_vibe.app_be.staff.auth.dto.LoginResponse;
import com.club_vibe.app_be.staff.auth.dto.RefreshTokenRequest;
import com.club_vibe.app_be.staff.auth.dto.RegisterRequest;
import com.club_vibe.app_be.staff.club.entity.ClubEntity;
import com.club_vibe.app_be.staff.staff.entity.StaffEntity;
import com.club_vibe.app_be.staff.staff.repository.StaffRepository;
import com.club_vibe.app_be.staff.staff.role.StaffRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    public LoginResponse register(RegisterRequest request) {
        StaffEntity newUser = switch (request.role()) {
            case CLUB -> {
                var builder = ClubEntity.builder();
                yield builder
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .name(request.name())
                        .role(StaffRole.CLUB)
                        .build();
            }
            case ARTIST -> {
                var builder = ArtistEntity.builder();
                yield builder
                        .email(request.email())
                        .password(passwordEncoder.encode(request.password()))
                        .stageName(request.name())
                        .role(StaffRole.ARTIST)
                        .build();
            }
        };

        StaffEntity savedUser = staffRepository.save(newUser);
        return generateAuthResponse(savedUser);
    }

    public LoginResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        StaffEntity user = staffRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(request.email()));

        return generateAuthResponse(user);
    }

    private LoginResponse generateAuthResponse(StaffEntity user) {
        return new LoginResponse(
                jwtService.generateToken(user.getStaffAuthDto()),
                jwtService.generateRefreshToken(user.getStaffAuthDto()),
                user.getEmail(),
                user.getRole(),
                user.getId()
        );
    }

    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String userEmail = jwtService.getEmail(request.refreshToken());
        StaffEntity user = staffRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));

        if (!jwtService.validateToken(request.refreshToken())) {
            throw new InvalidTokenException("Refresh token is invalid");
        }

        return new LoginResponse(
                jwtService.generateToken(user.getStaffAuthDto()),
                request.refreshToken(),
                user.getEmail(),
                user.getRole(),
                user.getId()
        );
    }
}