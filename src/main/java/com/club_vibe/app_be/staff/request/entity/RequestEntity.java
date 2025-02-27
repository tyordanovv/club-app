package com.club_vibe.app_be.staff.request.entity;

import com.club_vibe.app_be.events.entity.EventEntity;
import com.club_vibe.app_be.payments.entity.PaymentEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestEntity {
    public enum RequestType { SONG, GREETING }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RequestType type;

    private String songTitle;

    private String greetingMessage;

    @Email
    @NotBlank
    private String guestEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private PaymentEntity payment;
}
