package com.club_vibe.app_be.users.auth.service;

public interface CurrentUserService {

    /**
     * The method fetches the current authenticated user as custom implementation of UserDetails (StaffUserDetails)
     * from the context and returns the id.
     *
     * @return {@link Long} ID of the user
     */
    Long getCurrentUserId();
}