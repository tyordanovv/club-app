package com.club_vibe.app_be.stripe.payout.controller;

import com.club_vibe.app_be.common.enums.PayoutStatus;
import com.club_vibe.app_be.stripe.payout.dto.PayoutRequest;
import com.club_vibe.app_be.stripe.payout.dto.PayoutResponse;
import com.club_vibe.app_be.stripe.payout.dto.PayoutStatusResponse;
import com.club_vibe.app_be.stripe.payout.service.PayoutService;
import com.club_vibe.app_be.users.auth.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payouts")
@AllArgsConstructor
@Slf4j
public class PayoutProcessingController {
    private final PayoutService payoutService;
    private final CurrentUserService currentUserService;

    /**
     * Create a new payout for the authenticated user
     *
     * @param request The payout request
     * @return PayoutResponse with details of the created payout
     */
    @PostMapping
    public ResponseEntity<PayoutResponse> createPayout(@Valid @RequestBody PayoutRequest request) {
        log.info("Received request to create payout: {}", request);
        Long authenticatedUserId = currentUserService.getCurrentUserPlatformId();
        String userConnectedAccId = currentUserService.getCurrentUserStripeAccountId();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(payoutService.createPayout(request, authenticatedUserId, userConnectedAccId));
    }

    /**
     * Get all payouts for the authenticated user
     *
     * @return List of {@link PayoutResponse}
     */
    @GetMapping
    public ResponseEntity<List<PayoutResponse>> getUserPayouts() {
        Long authenticatedUserId = currentUserService.getCurrentUserPlatformId();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(payoutService.getUserPayouts(authenticatedUserId));
    }
}
