package com.club_vibe.app_be.users.auth.service.impl;

import com.club_vibe.app_be.users.auth.config.StaffUserDetails;
import com.club_vibe.app_be.users.auth.service.CurrentUserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {
    @Override
    public Long getCurrentUserPlatformId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof StaffUserDetails) {
            return ((StaffUserDetails) authentication.getPrincipal()).getProgramId();
        }
        throw new AccessDeniedException("User not authenticated");
    }
    @Override
    public String getCurrentUserStripeAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof StaffUserDetails) {
            return ((StaffUserDetails) authentication.getPrincipal()).getStripeAccountId();
        }
        throw new AccessDeniedException("User not authenticated");
    }
}