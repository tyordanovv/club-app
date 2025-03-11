package com.club_vibe.app_be.users.auth.controller;

import com.club_vibe.app_be.common.exception.EmailAlreadyExistsException;
import com.club_vibe.app_be.common.exception.ItemNotFoundException;
import com.club_vibe.app_be.rabbitmq.event.ConnectedAccountEvent;
import com.club_vibe.app_be.rabbitmq.producer.StripeProducer;
import com.club_vibe.app_be.users.auth.dto.AuthRequest;
import com.club_vibe.app_be.users.auth.dto.LoginResponse;
import com.club_vibe.app_be.users.auth.dto.RefreshTokenRequest;
import com.club_vibe.app_be.users.auth.dto.RegisterRequest;
import com.club_vibe.app_be.users.auth.service.impl.AuthServiceImpl;
import com.club_vibe.app_be.users.staff.repository.StaffRepository;
import com.club_vibe.app_be.users.staff.service.StaffService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;
    private final StaffService staffService;
    private final StripeProducer stripeProducer;

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        try {
            staffService.findStaffAuthByEmail(request.email());
        } catch (ItemNotFoundException ex) {
            LoginResponse response = authService.register(request);
            stripeProducer.publishConnectedAccountCreationEvent(
                    new ConnectedAccountEvent(request.country(), request.email()));
            return ResponseEntity.ok(response);
        }
        throw new EmailAlreadyExistsException(request.email());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody AuthRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @GetMapping("/")
    public ResponseEntity<String> testAuth(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok("Authenticated as: " + userDetails.getUsername());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('CLUB')")
    public ResponseEntity<String> adminOnly(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok("Hello CLUB: " + userDetails.getUsername());
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> userOnly(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok("Hello User: " + userDetails.getUsername());
    }
}