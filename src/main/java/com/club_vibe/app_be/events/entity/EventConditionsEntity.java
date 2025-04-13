package com.club_vibe.app_be.events.entity;

import com.club_vibe.app_be.common.util.DefaultPlatformValues;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "event_conditions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventConditionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "artist_percentage")
    private BigDecimal artistPercentage = BigDecimal.valueOf(0);

    @Column(nullable = false, name = "club_percentage")
    private BigDecimal clubPercentage = BigDecimal.valueOf(0);

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "enabled", column = @Column(name = "song_request_enabled")),
            @AttributeOverride(name = "minPrice", column = @Column(name = "song_request_min_price"))
    })
    private RequestSettings songRequestSettings = new RequestSettings();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "enabled", column = @Column(name = "greeting_request_enabled")),
            @AttributeOverride(name = "minPrice", column = @Column(name = "greeting_request_min_price"))
    })
    private RequestSettings greetingRequestSettings = new RequestSettings();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "enabled", column = @Column(name = "picture_request_enabled")),
            @AttributeOverride(name = "minPrice", column = @Column(name = "picture_request_min_price"))
    })
    private RequestSettings pictureRequestSettings = new RequestSettings();

    @Column(nullable = false, name = "max_requests")
    private Integer maxRequests;

    @PrePersist
    @PreUpdate
    private void validatePercentages() {
        BigDecimal sum = artistPercentage.add(clubPercentage);
        if (sum.compareTo(BigDecimal.valueOf(DefaultPlatformValues.PLATFORM_PAYED_AMOUNT)) != 0) {
            throw new IllegalStateException("Artist and club percentages must sum to DefaultPlatformValues.PLATFORM_PAYED_AMOUNT!");
        }
    }
}
