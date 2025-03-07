package com.club_vibe.app_be.users.auth.service.impl;

import com.club_vibe.app_be.users.auth.config.StaffUserDetails;
import com.club_vibe.app_be.users.staff.entity.StaffEntity;
import com.club_vibe.app_be.users.staff.repository.StaffRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component
@AllArgsConstructor
public class StaffDetailsServiceImpl implements UserDetailsService {
    private final StaffRepository staffRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        StaffEntity staff = staffRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Collection<GrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority(staff.getRole().toString())
        );

        return new StaffUserDetails(
                staff.getEmail(),
                staff.getPassword(),
                authorities,
                staff.getId()
        );
    }
}