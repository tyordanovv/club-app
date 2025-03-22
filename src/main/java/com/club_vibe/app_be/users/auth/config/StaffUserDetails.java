package com.club_vibe.app_be.users.auth.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class StaffUserDetails extends User {
    private final Long programId;
    private final String stripeAccountId;

    public StaffUserDetails(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities,
            Long programId,
            String stripeAccountId
    ) {
        super(username, password, authorities);
        this.programId = programId;
        this.stripeAccountId = stripeAccountId;
    }

}