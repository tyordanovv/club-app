package com.club_vibe.app_be.users.auth.service;

import com.club_vibe.app_be.users.auth.dto.AuthRequest;
import com.club_vibe.app_be.users.auth.dto.LoginResponse;
import com.club_vibe.app_be.users.auth.dto.RefreshTokenRequest;
import com.club_vibe.app_be.users.auth.dto.RegisterRequest;

public interface AuthService {
    /**
     *
     * @param request
     * @return
     */
    LoginResponse register(RegisterRequest request);

    /**
     *
     * @param request
     * @return
     */
    LoginResponse login(AuthRequest request);

    /**
     *
     * @param request
     * @return
     */
    LoginResponse refreshToken(RefreshTokenRequest request);
}