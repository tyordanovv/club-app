package com.club_vibe.app_be.stripe.balance.dto.artist;

import com.club_vibe.app_be.stripe.payments.entity.StripePaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ArtistPaymentDto{
    private Long paymentId;
    private LocalDateTime startTime;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime paymentDate;
    private String requestTitle;
    private StripePaymentStatus status;
}
