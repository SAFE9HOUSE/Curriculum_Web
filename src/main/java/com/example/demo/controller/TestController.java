package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/admin-only")
    public Map<String, Object> adminOnly() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Доступ разрешен! Это endpoint только для ADMIN");
        response.put("endpoint", "/api/test/admin-only");
        response.put("timestamp", new java.util.Date());
        
        if (auth != null) {
            response.put("username", auth.getName());
            response.put("authorities", auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList()));
        }
        
        return response;
    }
    
    // Для теста MANAGER
    @GetMapping("/manager-only")
    public Map<String, Object> managerOnly() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Доступ разрешен! Это endpoint только для MANAGER");
        response.put("endpoint", "/api/test/manager-only");
        return response;
    }
    
    // Для теста обоих ролей
    @GetMapping("/both-roles")
    public Map<String, Object> bothRoles() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Доступ разрешен! Это endpoint для ADMIN и MANAGER");
        response.put("endpoint", "/api/test/both-roles");
        return response;
    }
    
    // Простой тестовый endpoint (без проверки ролей)
    @GetMapping("/hello")
    public String hello() {
        return "Test endpoint works!";
    }
    
    // Проверка аутентификации
    @GetMapping("/check-auth")
    public Map<String, Object> checkAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        
        if (auth == null || !auth.isAuthenticated()) {
            response.put("authenticated", false);
            response.put("message", "Not authenticated");
        } else {
            response.put("authenticated", true);
            response.put("username", auth.getName());
            response.put("authorities", auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList()));
        }
        
        return response;
    }
}