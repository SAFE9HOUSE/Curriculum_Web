package com.example.demo.controller;

import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LoginResponse;
import com.example.demo.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.domain.auth.Admin;
import com.example.demo.dto.repository.AdminRepository; 

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping("/hash")
    public ResponseEntity<?> generateHash(@RequestParam String password) {
        try {
            String hash = passwordEncoder.encode(password);
            
            Map<String, String> response = Map.of(
                "password", password,
                "hash", hash,
                "sqlExample", String.format(
                    "INSERT INTO admins (username, password, role) VALUES " +
                    "('your_username', '%s', 'role');",
                    hash
                ),
                "warning", "NEVER store passwords in code/logs in production!"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error generating hash: ", e);
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to generate hash"));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        
        try {
            // ищем пользователя в БД
            Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("User not found: {}", request.getUsername());
                    return new RuntimeException("Invalid credentials");
                });
            
            // проверяем пароль
            if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                log.warn("Invalid password for user: {}", request.getUsername());
                throw new RuntimeException("Invalid credentials");
            }
            
            // генерируем токен с правильной ролью из enum
            String role = admin.getRole().name(); // "MANAGER" из AdminRole enum
            String token = jwtUtil.generateToken(admin.getUsername(), role);
            
            log.info("Login successful for user: {}, role: {}", admin.getUsername(), role);
            
            LoginResponse response = new LoginResponse(token, admin.getUsername(), role);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(401)
                .body(Map.of("error", "Invalid username or password"));
        } catch (Exception e) {
            log.error("Unexpected error during login: ", e);
            return ResponseEntity.status(500)
                .body(Map.of("error", "Internal server error"));
        }
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid authorization header");
        }
        
        String token = authHeader.substring(7);
        boolean isValid = jwtUtil.validateToken(token);
        
        if (isValid) {
            String username = jwtUtil.extractUsername(token);
            String role = jwtUtil.extractRole(token);
            long remainingMinutes = jwtUtil.getRemainingMinutes(token);
            
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "username", username,
                "role", role,
                "remainingMinutes", remainingMinutes
            ));
        } else {
            return ResponseEntity.ok(Map.of("valid", false));
        }
    }
}