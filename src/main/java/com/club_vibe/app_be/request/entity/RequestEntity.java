package com.club_vibe.app_be.request.entity;

import com.club_vibe.app_be.events.dto.RequestStatus;
import com.club_vibe.app_be.events.entity.EventEntity;
import com.club_vibe.app_be.stripe.payments.entity.PaymentEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "type")
    private RequestType type;

    @Column(nullable = false, name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @Column(name = "status")
    private RequestStatus status;

    @Email
    @NotBlank
    @Column(nullable = false, name = "guest_email")
    private String guestEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "event_id")
    private EventEntity event;

    @OneToOne(mappedBy = "request", cascade = CascadeType.ALL)
    private PaymentEntity payment;
}
