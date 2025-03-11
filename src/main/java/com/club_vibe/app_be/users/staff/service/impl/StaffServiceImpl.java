package com.club_vibe.app_be.users.staff.service.impl;

import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.users.auth.dto.StaffAuthenticationDTO;
import com.club_vibe.app_be.users.staff.dto.UpdateStripeDetailsRequest;
import com.club_vibe.app_be.users.staff.entity.KycStatus;
import com.club_vibe.app_be.users.staff.entity.StaffEntity;
import com.club_vibe.app_be.users.staff.entity.StripeDetails;
import com.club_vibe.app_be.users.staff.mapper.StaffMapper;
import com.club_vibe.app_be.users.staff.repository.StaffRepository;
import com.club_vibe.app_be.users.staff.service.StaffService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StaffServiceImpl implements StaffService {
    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    @Override
    public StaffAuthenticationDTO findStaffAuthByEmail(String email) throws ItemNotFoundException {
        StaffEntity staff = staffRepository.findByEmail(email)
                .orElseThrow(() -> new ItemNotFoundException(email));
        return staffMapper.mapStaffToAuthDTO(staff);
    }

    @Override
    public StaffAuthenticationDTO saveAndReturnDTO(StaffEntity newUser) {
        StaffEntity savedUser = staffRepository.save(newUser);
        return staffMapper.mapStaffToAuthDTO(savedUser);
    }

    @Override
    public void updateStripeDetails(UpdateStripeDetailsRequest updateStripeDetailsRequest) {
        StaffEntity staff = staffRepository.findByEmail(updateStripeDetailsRequest.email())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Staff not found with email: " + updateStripeDetailsRequest.email()));
        staff.setStripeDetails(
                new StripeDetails(
                        updateStripeDetailsRequest.stripeAccountId(),
                        updateStripeDetailsRequest.status()
                )
        );
        staffRepository.save(staff);
    }
}
