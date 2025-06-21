package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.service.JwtService;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secret.key}")
    private String secret_key;
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret_key.getBytes());
    }

    public  String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token=Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claim("role", userDetails.getAuthorities().stream()
                        .map(authority -> authority.getAuthority())
                        .toList())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact(); // Use getSigningKey()
        return token;
    }

    public String generateRefreshToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
        return token;
    }

    public String getUserNameFromJWT(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }
}

