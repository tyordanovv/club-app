package com.club_vibe.app_be.notification.service.impl;

import com.club_vibe.app_be.notification.dto.ClubNotificationRequest;
import com.club_vibe.app_be.notification.service.InviteNotificationService;
import com.club_vibe.app_be.users.artist.service.ArtistService;
import com.club_vibe.app_be.users.club.service.ClubService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ClubInviteNotificationService implements InviteNotificationService<ClubNotificationRequest> {
    private final ArtistService artistService;
    private final ClubService clubService;

    @Override
    public void notify(ClubNotificationRequest request) {

    }
}
