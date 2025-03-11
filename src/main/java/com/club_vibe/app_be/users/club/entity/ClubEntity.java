package com.club_vibe.app_be.users.club.entity;

import com.club_vibe.app_be.users.staff.entity.StaffEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

    @Column(unique = true)
    private String qrCodeIdentifier = UUID.randomUUID().toString();
}