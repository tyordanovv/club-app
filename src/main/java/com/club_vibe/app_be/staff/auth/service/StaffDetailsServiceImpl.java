package com.club_vibe.app_be.staff.auth.service;

import com.club_vibe.app_be.staff.staff.entity.StaffEntity;
import com.club_vibe.app_be.staff.staff.repository.StaffRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
@AllArgsConstructor
public class StaffDetailsServiceImpl implements UserDetailsService {
    private final StaffRepository staffRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        StaffEntity staff = staffRepository.findByEmail(email) .orElseThrow(() ->
                new UsernameNotFoundException("User not exists by Username or Email"));
        Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(staff.getRole().toString()));

        return new org.springframework.security.core.userdetails.User(
                email,
                staff.getPassword(),
                authorities
        );
    }
}