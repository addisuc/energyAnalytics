package com.weather.service.controller;

import com.weather.service.entity.Alert;
import com.weather.service.entity.AlertHistory;
import com.weather.service.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlertController {
    
    private final AlertService alertService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAlert(@RequestBody Map<String, Object> alertData) {
        try {
            // Mock user ID for testing
            Long userId = 1L;
            
            Alert alert = alertService.createAlert(userId, alertData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", alert);
            response.put("message", "Alert created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserAlerts() {
        try {
            // Mock user ID for testing
            Long userId = 1L;
            
            List<Alert> alerts = alertService.getUserAlerts(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", alerts);
            response.put("count", alerts.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PutMapping("/{alertId}")
    public ResponseEntity<Map<String, Object>> updateAlert(
            @PathVariable Long alertId,
            @RequestBody Map<String, Object> alertData) {
        try {
            Alert alert = alertService.updateAlert(alertId, alertData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", alert);
            response.put("message", "Alert updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @DeleteMapping("/{alertId}")
    public ResponseEntity<Map<String, Object>> deleteAlert(@PathVariable Long alertId) {
        try {
            alertService.deleteAlert(alertId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Alert deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/{alertId}/history")
    public ResponseEntity<Map<String, Object>> getAlertHistory(@PathVariable Long alertId) {
        try {
            List<AlertHistory> history = alertService.getAlertHistory(alertId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", history);
            response.put("count", history.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getAlertStatistics() {
        try {
            // Mock user ID for testing
            Long userId = 1L;
            
            Map<String, Object> stats = alertService.getAlertStatistics(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}