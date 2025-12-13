package com.example.demo.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    @Override
    @SuppressWarnings("null") 
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        
        log.info("🔐 JwtAuthFilter для: {}", request.getServletPath());
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7);
        
        try {
            username = jwtUtil.extractUsername(jwt);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtUtil.validateToken(jwt)) {
                    String role = jwtUtil.extractRole(jwt);
                    log.info("👤 User: {}, Role from token: {}", username, role);
                    
                    // 🔥 ВАЖНО: Spring Security требует префикс ROLE_
                    String springRole;
                    if (role == null || role.trim().isEmpty()) {
                        log.error("❌ Роль не найдена в токене");
                        filterChain.doFilter(request, response);
                        return;
                    }
                    
                    // Если роль уже с ROLE_, оставляем
                    if (role.startsWith("ROLE_")) {
                        springRole = role.toUpperCase(); // ROLE_ADMIN
                    } else {
                        // Добавляем ROLE_ префикс: ADMIN → ROLE_ADMIN
                        springRole = "ROLE_" + role.toUpperCase();
                    }
                    
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(springRole);
                    
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(authority)
                        );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.info("✅ Аутентификация установлена: {} - {}", username, springRole);
                }
            }
        } catch (Exception e) {
            log.error("❌ Ошибка: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
    
    @Override
    @SuppressWarnings("null")
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/") || 
               path.startsWith("/swagger-ui/") || 
               path.startsWith("/v3/api-docs/") ||
               path.equals("/error");
    }
}