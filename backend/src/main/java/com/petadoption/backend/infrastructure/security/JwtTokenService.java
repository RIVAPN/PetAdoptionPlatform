package com.petadoption.backend.infrastructure.security;

import com.petadoption.backend.core.domain.Role;
import com.petadoption.backend.core.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtTokenService {

    @Value("${security.jwt.secret:change-me-please-change-this-secret}")
    private String secret;

    @Value("${security.jwt.expire-hours:4}")
    private long expireHours;

    private Key getSigningKey() {
        // importante: a secret precisa ter um tamanho mínimo para HS256
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(User user) {
        Instant now = Instant.now();

        String roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(user.getEmail())  // usamos o email como "username" principal
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expireHours, ChronoUnit.HOURS)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmail(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
    }
}