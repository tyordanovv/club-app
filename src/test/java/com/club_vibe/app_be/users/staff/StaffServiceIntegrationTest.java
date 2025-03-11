package com.club_vibe.app_be.users.staff;

import com.club_vibe.app_be.users.auth.dto.StaffAuthenticationDTO;
import com.club_vibe.app_be.users.club.entity.ClubEntity;
import com.club_vibe.app_be.users.staff.repository.StaffRepository;
import com.club_vibe.app_be.users.staff.service.StaffService;
import com.club_vibe.app_be.users.staff.role.StaffRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class StaffServiceIntegrationTest {

    @Autowired
    private StaffService staffService;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String TEST_NAME = "Test Club";
    private static final String TEST_EMAIL = "test_user@club.com";
    private static final String TEST_PASSWORD = "password";

    @Test
    void testSaveAndFindStaff() {
        ClubEntity club = ClubEntity.builder()
                .email(TEST_EMAIL)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .name(TEST_NAME)
                .role(StaffRole.CLUB)
                .build();

        // Save the new club using the service
        StaffAuthenticationDTO savedDto = staffService.saveAndReturnDTO(club);
        assertNotNull(savedDto.id(), "Saved user should have an ID");

        // Retrieve the user by email and verify the details
        StaffAuthenticationDTO fetchedDto = staffService.findStaffAuthByEmail(TEST_EMAIL);
        assertEquals(savedDto.id(), fetchedDto.id(), "IDs should match");
        assertEquals(TEST_EMAIL, fetchedDto.email(), "Emails should match");
        assertEquals(StaffRole.CLUB, fetchedDto.role(), "Roles should match");
    }
}