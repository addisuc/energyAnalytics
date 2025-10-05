package com.weather.service.service;

import com.weather.service.entity.User;
import com.weather.service.repository.UserRepository;
import com.weather.service.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SubscriptionService subscriptionService;
    
    public Map<String, Object> authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));
            
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        String token = jwtUtil.generateToken(user.getId().toString());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", mapUserToResponse(user));
        return result;
    }
    
    public Map<String, Object> registerUser(String username, String email, String password, String firstName, String lastName) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRole("USER");
        user.setSubscriptionPlan("FREE");
        
        user = userRepository.save(user);
        subscriptionService.createFreeSubscription(user);
        
        String token = jwtUtil.generateToken(user.getId().toString());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", mapUserToResponse(user));
        return result;
    }
    
    private Map<String, Object> mapUserToResponse(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("email", user.getEmail());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("role", user.getRole());
        userMap.put("subscriptionPlan", user.getSubscriptionPlan());
        return userMap;
    }
}