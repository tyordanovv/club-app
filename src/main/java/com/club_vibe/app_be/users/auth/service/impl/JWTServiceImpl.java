package com.club_vibe.app_be.users.auth.service.impl;

import com.club_vibe.app_be.users.auth.dto.StaffAuthenticationDTO;
import com.club_vibe.app_be.users.auth.service.JWTService;
import com.club_vibe.app_be.users.staff.role.StaffRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JWTServiceImpl implements JWTService {

    private final Key signingKey;
    private final long jwtExpiration;
    private final long refreshExpiration;

    public JWTServiceImpl(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long jwtExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.jwtExpiration = jwtExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    @Override
    public String generateToken(StaffAuthenticationDTO authenticationDto) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + jwtExpiration);
        return generateToken(authenticationDto, expireDate, now);
    }

    @Override
    public String generateRefreshToken(StaffAuthenticationDTO authenticationDto) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpiration);
        return generateToken(authenticationDto, expireDate, now);
    }

    @Override
    public String getEmail(String token){

        return Jwts.parser()
                .verifyWith((SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public Long getUserId(String token) {
        return getClaim(token, claims -> claims.get("id", Long.class));
    }

    @Override
    public StaffRole getUserRole(String token) {
        String roleStr = getClaim(token, claims -> claims.get("role", String.class));
        return StaffRole.valueOf(roleStr);
    }

    @Override
    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | io.jsonwebtoken.MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.error("Unsupported JWT token: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    private <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String generateToken(StaffAuthenticationDTO authenticationDto, Date expireDate, Date issuedAt) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", authenticationDto.id());
        claims.put("role", authenticationDto.role().toString());

        return Jwts.builder()
                .claims(claims)
                .subject(authenticationDto.email())
                .issuedAt(issuedAt)
                .expiration(expireDate)
                .signWith(signingKey)
                .compact();
    }
}