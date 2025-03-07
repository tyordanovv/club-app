package com.club_vibe.app_be.users.staff.service.impl;

import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.users.auth.dto.StaffAuthenticationDTO;
import com.club_vibe.app_be.users.staff.entity.StaffEntity;
import com.club_vibe.app_be.users.staff.mapper.StaffMapper;
import com.club_vibe.app_be.users.staff.repository.StaffRepository;
import com.club_vibe.app_be.users.staff.service.StaffService;
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
}
