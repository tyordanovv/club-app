package com.club_vibe.app_be.payments.entity;

import com.club_vibe.app_be.common.enums.PayoutStatus;
import com.club_vibe.app_be.staff.club.entity.ClubEntity;
import com.club_vibe.app_be.staff.artist.entity.ArtistEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "payouts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayoutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    private String stripePayoutId;

    @Enumerated(EnumType.STRING)
    private PayoutStatus status = PayoutStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dj_id")
    private ArtistEntity dj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private ClubEntity club;
}