package com.club_vibe.app_be.users.club.entity;

import com.club_vibe.app_be.common.util.DefaultPlatformValues;
import com.club_vibe.app_be.events.entity.EventConditionsEntity;
import com.club_vibe.app_be.users.staff.entity.StaffEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "clubs")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClubEntity extends StaffEntity {
    @NotBlank
    @Column(unique = true)
    private String name;

    @Embedded
    private ClubAddress address;

    @Column(nullable = false, name = "artist_percentage")
    private BigDecimal artistPercentage;

    @Column(nullable = false, name = "club_percentage")
    private BigDecimal clubPercentage;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "default_conditions_id")
    private EventConditionsEntity defaultConditions;

    @Column(unique = true, updatable = false)
    private String qrCodeIdentifier = UUID.randomUUID().toString();
}