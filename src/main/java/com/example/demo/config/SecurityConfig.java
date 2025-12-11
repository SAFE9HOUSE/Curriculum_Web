package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Включаем CORS через Security
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. Отключаем CSRF (не нужно для REST API)
            .csrf(csrf -> csrf.disable())
            
            // 3. Настраиваем доступ к endpoints
            .authorizeHttpRequests(authz -> authz
                // Разрешаем публичный доступ к API
                .requestMatchers("/api/**").permitAll()
                
                // Разрешаем доступ к Swagger UI если есть
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                
                // Разрешаем доступ к H2 Console если используешь (для разработки)
                .requestMatchers("/h2-console/**").permitAll()
                
                // Всё остальное требует аутентификации
                .anyRequest().authenticated()
            );
        
        // Для H2 Console (если используешь)
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Разрешённые origin'ы
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",      // React dev server
            "http://127.0.0.1:3000",      // Альтернативный адрес
            "http://localhost:5173"       // Vite
        ));
        
        // Разрешённые методы
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Разрешённые заголовки
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Requested-With", 
            "Accept", 
            "Origin"
        ));
        
        // Заголовки, которые можно показывать клиенту
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Disposition"
        ));
        
        // Разрешить передачу cookies/authorization
        configuration.setAllowCredentials(true);
        
        // Время кэширования preflight запроса
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // Применяем CORS ко всем путям
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}