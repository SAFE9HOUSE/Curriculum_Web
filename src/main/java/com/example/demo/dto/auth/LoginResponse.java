package com.example.demo.dto.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String role;
    private Long expiresIn; // в секундах
    
    public LoginResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.expiresIn = 86400L; // 24 часа в секундах
    }
}