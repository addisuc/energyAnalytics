package com.weather.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitingService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    public boolean isAllowed(String userId, String subscriptionPlan) {
        String key = "rate_limit:" + userId;
        
        // Get rate limits based on subscription plan
        RateLimit rateLimit = getRateLimitForPlan(subscriptionPlan);
        
        // Check current usage
        String currentUsage = redisTemplate.opsForValue().get(key);
        int usage = currentUsage != null ? Integer.parseInt(currentUsage) : 0;
        
        if (usage >= rateLimit.getRequestsPerMinute()) {
            log.warn("Rate limit exceeded for user {} with plan {}: {} requests", userId, subscriptionPlan, usage);
            return false;
        }
        
        // Increment usage
        if (currentUsage == null) {
            redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(1));
        } else {
            redisTemplate.opsForValue().increment(key);
        }
        
        return true;
    }
    
    public RateLimit getRateLimitForPlan(String plan) {
        switch (plan.toUpperCase()) {
            case "FREE":
                return new RateLimit(10, 100);
            case "BASIC":
                return new RateLimit(60, 1000);
            case "PRO":
                return new RateLimit(300, 10000);
            case "ENTERPRISE":
                return new RateLimit(1000, -1); // Unlimited daily
            default:
                return new RateLimit(10, 100); // Default to FREE
        }
    }
    
    public int getRemainingRequests(String userId, String subscriptionPlan) {
        String key = "rate_limit:" + userId;
        String currentUsage = redisTemplate.opsForValue().get(key);
        int usage = currentUsage != null ? Integer.parseInt(currentUsage) : 0;
        
        RateLimit rateLimit = getRateLimitForPlan(subscriptionPlan);
        return Math.max(0, rateLimit.getRequestsPerMinute() - usage);
    }
    
    public long getResetTime(String userId) {
        String key = "rate_limit:" + userId;
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null ? ttl : 0;
    }
    
    public static class RateLimit {
        private final int requestsPerMinute;
        private final int requestsPerDay;
        
        public RateLimit(int requestsPerMinute, int requestsPerDay) {
            this.requestsPerMinute = requestsPerMinute;
            this.requestsPerDay = requestsPerDay;
        }
        
        public int getRequestsPerMinute() {
            return requestsPerMinute;
        }
        
        public int getRequestsPerDay() {
            return requestsPerDay;
        }
    }
}