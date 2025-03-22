package com.club_vibe.app_be.stripe.balance.dto.club;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ClubEarningDto {
    private Long eventId;
    private LocalDateTime eventDate;
    private BigDecimal amount;
    private String currencyCode;
    private Integer paymentCount;
    private LocalDateTime lastPaymentDate;
}
