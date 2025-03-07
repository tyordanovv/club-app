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


    @Test
    void testSaveAndFindStaff() {
        ClubEntity club = ClubEntity.builder()
                .email("club@example.com")
                .password(passwordEncoder.encode("secret"))
                .name("Club Name")
                .role(StaffRole.CLUB)
                .build();

        // Save the new club using the service
        StaffAuthenticationDTO savedDto = staffService.saveAndReturnDTO(club);
        assertNotNull(savedDto.id(), "Saved user should have an ID");

        // Retrieve the user by email and verify the details
        StaffAuthenticationDTO fetchedDto = staffService.findStaffAuthByEmail("club@example.com");
        assertEquals(savedDto.id(), fetchedDto.id(), "IDs should match");
        assertEquals("club@example.com", fetchedDto.email(), "Emails should match");
        assertEquals(StaffRole.CLUB, fetchedDto.role(), "Roles should match");
    }
}