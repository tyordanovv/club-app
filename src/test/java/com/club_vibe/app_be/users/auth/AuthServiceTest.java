package com.club_vibe.app_be.users.auth;

import com.club_vibe.app_be.common.exception.InvalidTokenException;
import com.club_vibe.app_be.users.artist.entity.ArtistEntity;
import com.club_vibe.app_be.users.auth.dto.AuthRequest;
import com.club_vibe.app_be.users.auth.dto.LoginResponse;
import com.club_vibe.app_be.users.auth.dto.RefreshTokenRequest;
import com.club_vibe.app_be.users.auth.dto.RegisterRequest;
import com.club_vibe.app_be.users.auth.dto.StaffAuthenticationDTO;
import com.club_vibe.app_be.users.auth.service.impl.AuthServiceImpl;
import com.club_vibe.app_be.users.auth.service.impl.JWTServiceImpl;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import com.club_vibe.app_be.users.staff.role.StaffRole;
import com.club_vibe.app_be.users.staff.service.StaffService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private StaffService staffService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JWTServiceImpl jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private static final Long USER_ID = 2L;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_NAME = "test-name";
    private static final String TEST_PASSWORD = "test-password";
    private static final String TEST_ACCESS_TOKEN = "auth-token";
    private static final String TEST_REFRESH_TOKEN = "refresh-token";


    @Test
    void registerClubSuccess() {
        // Arrange
        RegisterRequest request = new RegisterRequest(TEST_EMAIL, TEST_PASSWORD, TEST_NAME, StaffRole.CLUB);
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(encodedPassword);
        StaffAuthenticationDTO staffDto = new StaffAuthenticationDTO(USER_ID, TEST_EMAIL, StaffRole.CLUB);
        when(staffService.saveAndReturnDTO(any(ClubEntity.class))).thenReturn(staffDto);
        when(jwtService.generateToken(staffDto)).thenReturn(TEST_ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(staffDto)).thenReturn(TEST_REFRESH_TOKEN);

        // Act
        LoginResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_ACCESS_TOKEN, response.accessToken());
        assertEquals(TEST_REFRESH_TOKEN, response.refreshToken());
        assertEquals(TEST_EMAIL, response.email());
        assertEquals(StaffRole.CLUB, response.role());
        assertEquals(USER_ID, response.userId());
        ArgumentCaptor<ClubEntity> clubCaptor = ArgumentCaptor.forClass(ClubEntity.class);
        verify(staffService).saveAndReturnDTO(clubCaptor.capture());
        ClubEntity capturedEntity = clubCaptor.getValue();
        assertEquals(TEST_EMAIL, capturedEntity.getEmail());
        assertEquals(encodedPassword, capturedEntity.getPassword());
        assertEquals(TEST_NAME, capturedEntity.getName());
        assertEquals(StaffRole.CLUB, capturedEntity.getRole());
    }

    @Test
    void registerArtistSuccess() {
        // Arrange
        RegisterRequest request = new RegisterRequest(TEST_EMAIL, TEST_PASSWORD, TEST_NAME, StaffRole.ARTIST);
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(TEST_PASSWORD);
        StaffAuthenticationDTO staffDto = new StaffAuthenticationDTO(USER_ID, TEST_EMAIL, StaffRole.ARTIST);
        when(staffService.saveAndReturnDTO(any(ArtistEntity.class))).thenReturn(staffDto);
        when(jwtService.generateToken(staffDto)).thenReturn(TEST_ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(staffDto)).thenReturn(TEST_REFRESH_TOKEN);

        // Act
        LoginResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_ACCESS_TOKEN, response.accessToken());
        assertEquals(TEST_REFRESH_TOKEN, response.refreshToken());
        assertEquals(TEST_EMAIL, response.email());
        assertEquals(StaffRole.ARTIST, response.role());
        assertEquals(USER_ID, response.userId());

        ArgumentCaptor<ArtistEntity> artistCaptor = ArgumentCaptor.forClass(ArtistEntity.class);
        verify(staffService).saveAndReturnDTO(artistCaptor.capture());
        ArtistEntity capturedEntity = artistCaptor.getValue();
        assertEquals(TEST_EMAIL, capturedEntity.getEmail());
        assertEquals(TEST_PASSWORD, capturedEntity.getPassword());
        assertEquals(TEST_NAME, capturedEntity.getStageName());
        assertEquals(StaffRole.ARTIST, capturedEntity.getRole());
    }

    @Test
    void loginSuccess() {
        // Arrange
        AuthRequest authRequest = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);
        doNothing().when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        StaffAuthenticationDTO staffDto = new StaffAuthenticationDTO(USER_ID, TEST_EMAIL, StaffRole.CLUB);
        when(staffService.findStaffAuthByEmail(TEST_EMAIL)).thenReturn(staffDto);
        when(jwtService.generateToken(staffDto)).thenReturn(TEST_ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(staffDto)).thenReturn(TEST_REFRESH_TOKEN);

        // Act
        LoginResponse response = authService.login(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_ACCESS_TOKEN, response.accessToken());
        assertEquals(TEST_REFRESH_TOKEN, response.refreshToken());
        assertEquals(TEST_EMAIL, response.email());
        assertEquals(StaffRole.CLUB, response.role());
        assertEquals(USER_ID, response.userId());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> authCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(authCaptor.capture());
        UsernamePasswordAuthenticationToken capturedToken = authCaptor.getValue();
        assertEquals(TEST_EMAIL, capturedToken.getPrincipal());
        assertEquals(TEST_PASSWORD, capturedToken.getCredentials());
    }

    @Test
    void refreshTokenSuccess() {
        // Arrange
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(TEST_REFRESH_TOKEN);

        when(jwtService.getEmail(TEST_REFRESH_TOKEN)).thenReturn(TEST_EMAIL);
        when(jwtService.validateToken(TEST_REFRESH_TOKEN)).thenReturn(true);

        StaffAuthenticationDTO staffDto = new StaffAuthenticationDTO(USER_ID, TEST_EMAIL, StaffRole.ARTIST);
        when(staffService.findStaffAuthByEmail(TEST_EMAIL)).thenReturn(staffDto);
        when(jwtService.generateToken(staffDto)).thenReturn(TEST_ACCESS_TOKEN);

        // Act
        LoginResponse response = authService.refreshToken(refreshRequest);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_ACCESS_TOKEN, response.accessToken());
        assertEquals(TEST_REFRESH_TOKEN, response.refreshToken());
        assertEquals(TEST_EMAIL, response.email());
        assertEquals(StaffRole.ARTIST, response.role());
        assertEquals(USER_ID, response.userId());
    }

    @Test
    void refreshTokenInvalidThrowsException() {
        // Arrange
        String invalidRefreshToken = "invalidRefreshToken";
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(invalidRefreshToken);
        when(jwtService.validateToken(invalidRefreshToken)).thenReturn(false);

        // Act and Assert
        InvalidTokenException exception = assertThrows(InvalidTokenException.class,
                () -> authService.refreshToken(refreshRequest));
        assertEquals("Refresh token is invalid", exception.getMessage());
    }
}