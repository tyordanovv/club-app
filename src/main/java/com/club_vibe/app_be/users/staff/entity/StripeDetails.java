package com.club_vibe.app_be.users.staff.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripeDetails {
    @Column(unique = true)
    private String accountId;

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;
}
