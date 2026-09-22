package com.civicvote.result.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractUsername(String token) { return extractClaims(token).getSubject(); }
    public String extractRole(String token) { return (String) extractClaims(token).get("role"); }

    public boolean isTokenValid(String token) {
        try { return !extractClaims(token).getExpiration().before(new Date()); }
        catch (Exception e) { return false; }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }
}
