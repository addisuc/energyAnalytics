package com.weather.service.config;

import com.weather.service.repository.UserRepository;
import com.weather.service.config.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    
    private String extractUserIdFromToken(String token) {
        try {
            return jwtUtil.extractUsername(token);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                // Handle demo token for backward compatibility
                if ("demo-token-12345".equals(token)) {
                    userRepository.findByEmail("demo@weather.com")
                        .ifPresent(user -> {
                            UsernamePasswordAuthenticationToken authentication = 
                                new UsernamePasswordAuthenticationToken(user.getId().toString(), null, new ArrayList<>());
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        });
                } else {
                    // Real JWT validation
                    String userId = extractUserIdFromToken(token);
                    if (userId != null && userRepository.existsById(Long.parseLong(userId))) {
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                // Invalid token - continue without authentication
            }
        }
        
        filterChain.doFilter(request, response);
    }
}