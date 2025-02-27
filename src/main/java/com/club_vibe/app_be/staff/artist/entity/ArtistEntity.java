package com.club_vibe.app_be.staff.artist.entity;

import com.club_vibe.app_be.staff.staff.entity.StaffEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "artists")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArtistEntity extends StaffEntity {
    @NotBlank
    @Column(unique = true)
    private String stageName;

    private String stripeAccountId;
    private boolean stripeVerified = false;
}
