package com.club_vibe.app_be.users.staff.mapper;

import com.club_vibe.app_be.users.auth.dto.StaffAuthenticationDTO;
import com.club_vibe.app_be.users.staff.entity.StaffEntity;
import org.springframework.stereotype.Component;

@Component
public class StaffMapper {
    public StaffAuthenticationDTO mapStaffToAuthDTO(StaffEntity staff){
        return new StaffAuthenticationDTO(
                staff.getId(),
                staff.getEmail(),
                staff.getRole()
        );
    }
}
