package com.club_vibe.app_be.stripe.payments.entity.partial;

import com.club_vibe.app_be.stripe.payments.entity.PaymentEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_allocations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartialPaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentAllocationType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private LocalDateTime allocationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;

    private String description;
}