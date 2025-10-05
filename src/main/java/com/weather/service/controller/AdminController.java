package com.weather.service.controller;

import com.weather.service.repository.WeatherDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final WeatherDataRepository weatherDataRepository;
    private final CacheManager cacheManager;
    
    @DeleteMapping("/clear-cache")
    public ResponseEntity<String> clearCache() {
        cacheManager.getCacheNames().forEach(cacheName -> 
            cacheManager.getCache(cacheName).clear());
        return ResponseEntity.ok("Cache cleared successfully");
    }
    
    @DeleteMapping("/clear-database")
    public ResponseEntity<String> clearDatabase() {
        weatherDataRepository.deleteAll();
        return ResponseEntity.ok("Database cleared successfully");
    }
    
    @DeleteMapping("/clear-all")
    public ResponseEntity<String> clearAll() {
        // Clear cache
        cacheManager.getCacheNames().forEach(cacheName -> 
            cacheManager.getCache(cacheName).clear());
        
        // Clear database
        weatherDataRepository.deleteAll();
        
        return ResponseEntity.ok("Cache and database cleared successfully");
    }
}