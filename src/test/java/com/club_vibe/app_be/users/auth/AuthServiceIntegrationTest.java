package com.club_vibe.app_be.users.auth;

import com.club_vibe.app_be.users.auth.dto.AuthRequest;
import com.club_vibe.app_be.users.auth.dto.LoginResponse;
import com.club_vibe.app_be.users.auth.service.AuthService;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import com.club_vibe.app_be.users.staff.repository.StaffRepository;
import com.club_vibe.app_be.users.staff.role.StaffRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_NAME = "Test Club";
    private static final String TEST_EMAIL = "test@club.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String EXPECTED_ERROR_MESSAGE = "Bad credentials";

    @BeforeEach
    void setUp() {
        staffRepository.deleteByEmail(TEST_EMAIL);
        assertFalse(staffRepository.findByEmail(TEST_EMAIL).isPresent(),
                "Test email should not be present in the current database state.");

        ClubEntity club = ClubEntity.builder()
                .email(TEST_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .name(TEST_NAME)
                .role(StaffRole.CLUB)
                .build();
        staffRepository.save(club);
    }

    @Test
    void testSuccessfulLogin() {
        AuthRequest request = new AuthRequest(TEST_EMAIL, TEST_PASSWORD);
        LoginResponse response = authService.login(request);

        assertNotNull(response, "Login response should not be null");
        assertNotNull(response.userId(), "Id of the user should be generated");
        assertNotNull(response.accessToken(), "Access token should be generated");
        assertNotNull(response.refreshToken(), "Refresh token should be generated");
        assertEquals(TEST_EMAIL, response.email());
        assertEquals(StaffRole.CLUB, response.role());
    }

    @Test
    void testFailedLogin_InvalidPassword() {
        AuthRequest request = new AuthRequest(TEST_EMAIL, "wrongpassword");

        Exception exception = assertThrows(BadCredentialsException.class, () -> {
            authService.login(request);
        });

        assertEquals(EXPECTED_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    void testFailedLogin_NonExistentUser() {
        AuthRequest request = new AuthRequest("wrongemail@club.com", TEST_PASSWORD);

        Exception exception = assertThrows(BadCredentialsException.class, () -> {
            authService.login(request);
        });

        assertEquals(EXPECTED_ERROR_MESSAGE, exception.getMessage());
    }
}