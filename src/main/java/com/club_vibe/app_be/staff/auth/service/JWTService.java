package com.club_vibe.app_be.staff.auth.service;

import com.club_vibe.app_be.staff.staff.entity.StaffEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JWTService {

    private final Key signingKey;
    private final long jwtExpiration;
    private final long refreshExpiration;

    public JWTService(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration}") long jwtExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.jwtExpiration = jwtExpiration;
        this.refreshExpiration = refreshExpiration;
    }
    public String generateToken(StaffEntity.StaffAuthenticationDTO authenticationDto) {
        Date expireDate = new Date(new Date().getTime() + jwtExpiration);
        return generateToken(authenticationDto, expireDate);
    }

    public String generateRefreshToken(StaffEntity.StaffAuthenticationDTO authenticationDto) {
        Date expireDate = new Date(new Date().getTime() + refreshExpiration);
        return generateToken(authenticationDto, expireDate);
    }

    private String generateToken(StaffEntity.StaffAuthenticationDTO authenticationDto, Date expireDate) {
        String email = authenticationDto.email();
        Date currentDate = new Date();

        return Jwts.builder()
                .subject(email)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(signingKey)
                .compact();
    }


    public String getEmail(String token){

        return Jwts.parser()
                .verifyWith((SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | io.jsonwebtoken.MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            System.out.println("Unsupported JWT token: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }
}