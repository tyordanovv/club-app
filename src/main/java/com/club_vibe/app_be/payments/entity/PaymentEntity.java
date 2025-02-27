package com.club_vibe.app_be.payments.entity;

import com.club_vibe.app_be.staff.request.entity.RequestEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(unique = true)
    private String stripePaymentIntentId;

    private LocalDateTime timestamp = LocalDateTime.now();

    @OneToOne(mappedBy = "payment")
    private RequestEntity request;
}
