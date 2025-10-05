package com.weather.service.controller;

import com.weather.service.entity.Subscription;
import com.weather.service.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentSubscription(Authentication auth) {
        // Get user ID from authentication (set by JWT filter)
        String userId = auth.getName();
        
        // Return subscription for authenticated user
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", Map.of(
                "plan", "PRO",
                "status", "ACTIVE",
                "usageLimit", 10000,
                "currentUsage", 150,
                "startDate", java.time.LocalDateTime.now().toString(),
                "userId", userId
            )
        ));
    }
    
    @GetMapping("/plans")
    public ResponseEntity<Map<String, Object>> getAvailablePlans() {
        java.util.Map<String, Object> plans = new java.util.LinkedHashMap<>();
        plans.put("FREE", Map.of("price", 0, "apiLimit", 100, "regions", 1));
        plans.put("BASIC", Map.of("price", 29, "apiLimit", 1000, "regions", 5));
        plans.put("PRO", Map.of("price", 99, "apiLimit", 10000, "regions", 20));
        plans.put("ENTERPRISE", Map.of("price", 299, "apiLimit", "unlimited", "regions", "unlimited"));
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", plans
        ));
    }
}