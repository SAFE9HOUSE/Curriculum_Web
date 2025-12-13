package com.example.demo.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {
    
    private final String SECRET_KEY = "education-system-secret-key-2025-for-university-app-123";
    private final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24 часа
    
    public JwtUtil() {
        log.info("JWT Utility initialized with key length: {} bytes", 
                 SECRET_KEY.getBytes(StandardCharsets.UTF_8).length);
    }
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        
        if (keyBytes.length < 32) {
            log.warn("Secret key is only {} bytes. Recommended minimum is 32 bytes for HS256", 
                     keyBytes.length);
        }
        
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public String generateToken(String username, String role) {
        log.debug("Generating token for user: {}, role: {}", username, role);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    public Boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token expired: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
            return false;
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    public long getRemainingMinutes(String token) {
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            Date now = new Date();
            long diff = expiration.getTime() - now.getTime();
            return diff / (60 * 1000);
        } catch (Exception e) {
            return -1;
        }
    }
}