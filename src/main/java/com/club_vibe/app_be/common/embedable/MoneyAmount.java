package com.club_vibe.app_be.common.embedable;

import com.club_vibe.app_be.users.staff.entity.KycStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyAmount {
    @Column(nullable = false, precision = 10, scale = 2, name = "amount")
    private BigDecimal amount;

    @Column(nullable = false, name = "currency")
    private String currency;
}

