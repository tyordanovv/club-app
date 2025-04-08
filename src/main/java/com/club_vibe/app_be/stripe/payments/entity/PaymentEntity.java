package com.club_vibe.app_be.stripe.payments.entity;

import com.club_vibe.app_be.request.entity.RequestEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @Column(nullable = false, precision = 10, scale = 2, name = "amount")
    private BigDecimal amount;

    @Column(nullable = false, name = "currency")
    private String currency;

    @Column(nullable = false, unique = true, name = "stripe_intent_id")
    private String stripePaymentIntentId;


    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(nullable = false, name = "stripe_payment_status")
    private StripePaymentStatus status;

    @OneToOne
    @JoinColumn(nullable = false, name = "request_id", referencedColumnName = "id")
    private RequestEntity request;
}
