package com.club_vibe.app_be.users.auth.service;

import java.util.Optional;

public interface CurrentUserService {

    /**
     * The method fetches the current authenticated user as custom implementation of UserDetails (StaffUserDetails)
     * from the context and returns the platform id.
     *
     * @return {@link Long} ID of the user
     */
    Long getCurrentUserPlatformId();

    /**
     * The method fetches the current authenticated user as custom implementation of UserDetails (StaffUserDetails)
     * from the context and returns the stripe id, which is used to identify the current user in Stripe environment.
     *
     * @return {@link String} Stripe account id of the user
     */
    String getCurrentUserStripeAccountId();
}