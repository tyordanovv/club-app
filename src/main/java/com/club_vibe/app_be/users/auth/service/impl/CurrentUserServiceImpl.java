package com.club_vibe.app_be.users.auth.service.impl;

import com.club_vibe.app_be.events.service.EventService;
import com.club_vibe.app_be.users.auth.config.StaffUserDetails;
import com.club_vibe.app_be.users.auth.service.CurrentUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {
    private final EventService eventService;

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