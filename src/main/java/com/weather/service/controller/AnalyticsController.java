package com.weather.service.controller;

import com.weather.service.service.EnergyAnalyticsService;
import com.weather.service.service.SubscriptionService;
import com.weather.service.service.HistoricalDataService;
import com.weather.service.service.ForecastingService;
import com.weather.service.service.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@Tag(name = "Analytics", description = "Real-time energy analytics and dashboard data")
public class AnalyticsController {
    
    private final EnergyAnalyticsService analyticsService;
    private final SubscriptionService subscriptionService;
    private final HistoricalDataService historicalDataService;
    private final ForecastingService forecastingService;
    private final CacheService cacheService;
    
    @Operation(
        summary = "Get Energy Dashboard Data",
        description = "Retrieves comprehensive energy analytics data for a specific region including generation, consumption, and efficiency metrics."
    )
    @GetMapping("/energy/{region}")
    public ResponseEntity<Map<String, Object>> getEnergyDashboard(
        @Parameter(description = "Region identifier (north, south, east, west, central)") 
        @PathVariable String region,
        Authentication authentication) {
        
        // Validate subscription access only if authenticated
        if (authentication != null) {
            Long userId = Long.parseLong(authentication.getName());
            if (!subscriptionService.hasApiAccess(userId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Access denied. Check your subscription plan.");
                return ResponseEntity.status(403).body(errorResponse);
            }
            subscriptionService.incrementUsage(userId);
        }
        // Allow access without authentication for demo purposes
        
        try {
            // Check cache first
            String cacheKey = "energy_dashboard:" + region;
            Object cachedData = cacheService.getCachedApiResponse(cacheKey);
            
            if (cachedData != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", cachedData);
                response.put("timestamp", java.time.LocalDateTime.now().toString());
                response.put("cached", true);
                return ResponseEntity.ok(response);
            }
            
            Map<String, Object> dashboard = analyticsService.getEnergyDashboard(region);
            
            // Cache the result for 5 minutes
            cacheService.cacheApiResponse(cacheKey, dashboard, java.time.Duration.ofMinutes(5));
            
            // Wrap in API response format
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", dashboard);
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/weather-correlation/{region}")
    public ResponseEntity<Map<String, Object>> getWeatherCorrelation(
            @PathVariable String region,
            @RequestParam(defaultValue = "24h") String timeRange,
            Authentication authentication) {
        
        // Allow access without authentication for demo
        if (authentication != null) {
            Long userId = Long.parseLong(authentication.getName());
//            if (!subscriptionService.hasApiAccess(userId)) {
//                Map<String, Object> errorResponse = new HashMap<>();
//                errorResponse.put("success", false);
//                errorResponse.put("error", "Access denied. Check your subscription plan.");
//                return ResponseEntity.status(403).body(errorResponse);
//            }
            subscriptionService.incrementUsage(userId);
        }
        
        try {
            Map<String, Object> correlation = analyticsService.getWeatherCorrelation(region, timeRange);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", correlation);
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/predictive/{region}")
    public ResponseEntity<Map<String, Object>> getPredictiveAnalytics(
            @PathVariable String region,
            @RequestParam(defaultValue = "24h") String forecastPeriod,
            Authentication authentication) {
        
        if (authentication != null) {
            Long userId = Long.parseLong(authentication.getName());
//            if (!subscriptionService.hasApiAccess(userId)) {
//                Map<String, Object> errorResponse = new HashMap<>();
//                errorResponse.put("success", false);
//                errorResponse.put("error", "Access denied. Check your subscription plan.");
//                return ResponseEntity.status(403).body(errorResponse);
//            }
            subscriptionService.incrementUsage(userId);
        }
        
        try {
            Map<String, Object> predictive = analyticsService.getPredictiveAnalytics(region, forecastPeriod);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", predictive);
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/trends/{region}")
    public ResponseEntity<?> getEnergyTrends(
            @PathVariable String region,
            @RequestParam(defaultValue = "7") int days) {
        
        // This will return empty data for now
        var trends = analyticsService.getEnergyTrends(region, days);
        
        if (trends.isEmpty()) {
            // TODO: Better response when no data available
            return ResponseEntity.ok(Map.of("message", "Trends feature coming soon"));
        }
        
        return ResponseEntity.ok(trends);
    }
    
    @GetMapping("/historical/{region}")
    public ResponseEntity<Map<String, Object>> getHistoricalAnalytics(
            @PathVariable String region,
            @RequestParam(defaultValue = "7") int days,
            Authentication authentication) {
        
        if (authentication != null) {
            Long userId = Long.parseLong(authentication.getName());
//            if (!subscriptionService.hasApiAccess(userId)) {
//                Map<String, Object> errorResponse = new HashMap<>();
//                errorResponse.put("success", false);
//                errorResponse.put("error", "Access denied. Check your subscription plan.");
//                return ResponseEntity.status(403).body(errorResponse);
//            }
            subscriptionService.incrementUsage(userId);
        }
        
        try {
            Map<String, Object> historical = historicalDataService.getHistoricalAnalytics(region, days);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", historical);
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}