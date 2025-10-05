package com.weather.service.controller;

import com.weather.service.service.ForecastingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ForecastController {
    
    private final ForecastingService forecastingService;
    
    @GetMapping("/demand/{region}")
    public ResponseEntity<Map<String, Object>> getDemandForecast(
            @PathVariable String region,
            @RequestParam(defaultValue = "7") int days) {
        try {
            Map<String, Object> forecast = forecastingService.generateEnergyDemandForecast(region, days);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", forecast);
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/generation/{region}")
    public ResponseEntity<Map<String, Object>> getGenerationForecast(
            @PathVariable String region,
            @RequestParam(defaultValue = "7") int days) {
        try {
            Map<String, Object> forecast = forecastingService.generateWeatherBasedGenerationForecast(region, days);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", forecast);
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/price/{region}")
    public ResponseEntity<Map<String, Object>> getPriceForecast(
            @PathVariable String region,
            @RequestParam(defaultValue = "7") int days) {
        try {
            Map<String, Object> forecast = forecastingService.generatePriceForecast(region, days);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", forecast);
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/comprehensive/{region}")
    public ResponseEntity<Map<String, Object>> getComprehensiveForecast(
            @PathVariable String region,
            @RequestParam(defaultValue = "7") int days) {
        try {
            Map<String, Object> forecast = forecastingService.getComprehensiveForecast(region, days);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", forecast);
            response.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}