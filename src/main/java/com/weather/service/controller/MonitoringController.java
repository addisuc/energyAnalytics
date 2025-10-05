package com.weather.service.controller;

import com.weather.service.service.MetricsService;
import com.weather.service.service.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
@Tag(name = "Monitoring", description = "System monitoring and health endpoints")
@CrossOrigin(origins = "*")
public class MonitoringController {

    private final MetricsService metricsService;
    private final CacheService cacheService;

    @GetMapping("/health")
    @Operation(summary = "Get system health status")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", "Energy Analytics Platform");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/metrics/business")
    @Operation(summary = "Get business metrics dashboard")
    public ResponseEntity<Map<String, Object>> getBusinessMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Simulate business metrics (in production, fetch from metrics registry)
        metrics.put("active_users", cacheService.getCounter("active_users"));
        metrics.put("api_calls_today", cacheService.getCounter("api_calls_today"));
        metrics.put("revenue_today", 1250.75);
        metrics.put("subscription_conversions", 12);
        metrics.put("cache_hit_rate", 0.85);
        metrics.put("avg_response_time", 245);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", metrics);
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/metrics/performance")
    @Operation(summary = "Get performance metrics")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Performance metrics
        metrics.put("database_connections", 15);
        metrics.put("redis_connections", 8);
        metrics.put("memory_usage_mb", 512);
        metrics.put("cpu_usage_percent", 25.5);
        metrics.put("disk_usage_percent", 45.2);
        metrics.put("network_io_mbps", 12.3);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", metrics);
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/alert")
    @Operation(summary = "Trigger monitoring alert")
    public ResponseEntity<Map<String, Object>> triggerAlert(@RequestBody Map<String, Object> alertData) {
        String alertType = (String) alertData.get("type");
        String message = (String) alertData.get("message");
        String severity = (String) alertData.get("severity");
        
        // Log alert
        Map<String, Object> alert = new HashMap<>();
        alert.put("timestamp", LocalDateTime.now().toString());
        alert.put("type", alertType);
        alert.put("message", message);
        alert.put("severity", severity);
        
        // Record alert metric
        metricsService.recordSecurityEvent(alertType, severity);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("alertId", java.util.UUID.randomUUID().toString());
        response.put("message", "Alert recorded successfully");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/uptime")
    @Operation(summary = "Get system uptime information")
    public ResponseEntity<Map<String, Object>> getUptime() {
        Map<String, Object> uptime = new HashMap<>();
        
        // System uptime (simplified)
        long uptimeMs = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
        long uptimeHours = uptimeMs / (1000 * 60 * 60);
        
        uptime.put("uptime_hours", uptimeHours);
        uptime.put("uptime_ms", uptimeMs);
        uptime.put("start_time", LocalDateTime.now().minusSeconds(uptimeMs / 1000).toString());
        uptime.put("current_time", LocalDateTime.now().toString());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", uptime);
        
        return ResponseEntity.ok(response);
    }
}