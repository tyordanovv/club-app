package com.club_vibe.app_be.users.staff.service;

import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.users.auth.dto.StaffAuthenticationDTO;
import com.club_vibe.app_be.users.staff.dto.UpdateStripeDetailsRequest;
import com.club_vibe.app_be.users.staff.entity.KycStatus;
import com.club_vibe.app_be.users.staff.entity.StaffEntity;

/**
 *
 */
public interface StaffService {
    String NAME = "Staff";
    /**
     *
     * @param email {@link String} Email of the user
     * @return {@link StaffAuthenticationDTO}
     * @throws ItemNotFoundException
     */
    StaffAuthenticationDTO findStaffAuthByEmail(String email) throws ItemNotFoundException;

    /**
     *
     * @param staff
     * @return {@link StaffAuthenticationDTO}
     */
    StaffAuthenticationDTO saveAndReturnDTO(StaffEntity staff);

    /**
     *
     * @param updateStripeDetailsRequest {@link UpdateStripeDetailsRequest}
     */
    void updateStripeDetails(UpdateStripeDetailsRequest updateStripeDetailsRequest);
}
