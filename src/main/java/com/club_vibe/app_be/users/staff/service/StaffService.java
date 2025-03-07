package com.club_vibe.app_be.users.staff.service;

import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.users.auth.dto.StaffAuthenticationDTO;
import com.club_vibe.app_be.users.staff.entity.StaffEntity;

public interface StaffService {
    /**
     *
     * @param email
     * @return
     * @throws ItemNotFoundException
     */
    StaffAuthenticationDTO findStaffAuthByEmail(String email) throws ItemNotFoundException;

    /**
     *
     * @param staff
     * @return
     */
    StaffAuthenticationDTO saveAndReturnDTO(StaffEntity staff);
}
