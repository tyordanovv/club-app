package com.club_vibe.app_be.events.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
public class RequestSettings {
    private boolean enabled = false;
    private BigDecimal minPrice = BigDecimal.ZERO;
}