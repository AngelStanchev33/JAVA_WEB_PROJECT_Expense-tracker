package com.example.expense_tracker.service.impl;

import com.example.expense_tracker.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    private final String jwtSecret;
    private final long expiration;

    public JwtServiceImpl(@Value("${jwt.signing-key}") String jwtSecret,
                          @Value("${jwt.expiration}") long expiration) {
        this.jwtSecret = jwtSecret;
        this.expiration = expiration;
    }

    @Override
    public String generateToken(String email, Map<String, Object> claims) {

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("email cant be null ro empty");
        }

        var now = new Date();
        return Jwts
                .builder()
                .claims(claims)
                .subject(email)
                .issuedAt(now)
                .notBefore(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String extractEmail(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    // Проверява дали token е валиден (format + signature + expiration)
    @Override
    public boolean isTokenValid(String token) {
        try {
            // Ако стигне дотук без exception - token format и signature са OK
            // Остава само да проверим дали не е изтекъл
            return !isTokenExpired(token);
        } catch (Exception e) {
            // Всяка грешка означава невалиден token:
            // - MalformedJwtException (грешен format)
            // - SignatureException (грешен подпис) 
            // - ExpiredJwtException (изтекъл)
            // - IllegalArgumentException (null/празен)
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }

    private Date getExpirationDate(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);  // Взима всички данни от token-а
        return claimsResolver.apply(claims);            // Прилага функцията за извличане на конкретното поле
    }


    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
