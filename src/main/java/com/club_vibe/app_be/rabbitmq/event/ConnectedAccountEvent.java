package com.club_vibe.app_be.rabbitmq.event;

import com.club_vibe.app_be.common.enums.Country;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ConnectedAccountEvent {
    private Country country;
    private String email;
}
