package com.weather.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Cacheable(value = "weather-data", key = "#city")
    public Object getWeatherData(String city) {
        // This will be called only if not in cache
        log.info("Cache miss for weather data: {}", city);
        return null; // Actual service will populate this
    }

    @Cacheable(value = "energy-analytics", key = "#region + '_' + #timeRange")
    public Object getEnergyAnalytics(String region, String timeRange) {
        log.info("Cache miss for energy analytics: {} - {}", region, timeRange);
        return null;
    }

    @CacheEvict(value = "weather-data", key = "#city")
    public void evictWeatherData(String city) {
        log.info("Evicting weather data cache for: {}", city);
    }

    @CacheEvict(value = "energy-analytics", allEntries = true)
    public void evictAllEnergyAnalytics() {
        log.info("Evicting all energy analytics cache");
    }

    // Manual cache operations for complex scenarios
    public void cacheApiResponse(String key, Object data, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, data, ttl);
            log.debug("Cached API response: {}", key);
        } catch (Exception e) {
            log.error("Failed to cache API response: {}", key, e);
        }
    }

    public Object getCachedApiResponse(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Failed to get cached API response: {}", key, e);
            return null;
        }
    }

    public void cacheUserSession(String userId, Map<String, Object> sessionData) {
        try {
            String key = "user_session:" + userId;
            redisTemplate.opsForHash().putAll(key, sessionData);
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Failed to cache user session: {}", userId, e);
        }
    }

    public Map<Object, Object> getUserSession(String userId) {
        try {
            String key = "user_session:" + userId;
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("Failed to get user session: {}", userId, e);
            return null;
        }
    }

    public void incrementCounter(String key) {
        try {
            redisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            log.error("Failed to increment counter: {}", key, e);
        }
    }

    public Long getCounter(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? Long.valueOf(value.toString()) : 0L;
        } catch (Exception e) {
            log.error("Failed to get counter: {}", key, e);
            return 0L;
        }
    }
}