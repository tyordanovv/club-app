package com.club_vibe.app_be.stripe.balance.controller;

import com.club_vibe.app_be.stripe.balance.dto.artist.ArtistBalanceResponse;
import com.club_vibe.app_be.stripe.balance.dto.club.ClubBalanceResponse;
import com.club_vibe.app_be.stripe.balance.servce.BalanceService;
import com.club_vibe.app_be.users.auth.service.CurrentUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/balance")
@AllArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping("/artist")
    public ResponseEntity<ArtistBalanceResponse> getArtistBalance() {
        ArtistBalanceResponse balance = balanceService.getArtistBalance();
        return ResponseEntity.ok(balance);
    }

    @GetMapping("/club")
    public ResponseEntity<ClubBalanceResponse> getClubBalance() {
        ClubBalanceResponse balance = balanceService.getClubBalance();
        return ResponseEntity.ok(balance);
    }
}