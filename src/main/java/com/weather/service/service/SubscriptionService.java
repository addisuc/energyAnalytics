package com.weather.service.service;

import com.weather.service.entity.Subscription;
import com.weather.service.entity.User;
import com.weather.service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;
    
    public Subscription createFreeSubscription(User user) {
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(Subscription.SubscriptionPlan.FREE);
        subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setUsageLimit(100);
        subscription.setCurrentUsage(0);
        return subscriptionRepository.save(subscription);
    }
    
    public Optional<Subscription> getByUserId(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }
    
    public boolean hasApiAccess(Long userId) {
        return getByUserId(userId)
            .map(sub -> sub.getStatus() == Subscription.SubscriptionStatus.ACTIVE && 
                       (sub.getUsageLimit() == -1 || sub.getCurrentUsage() < sub.getUsageLimit()))
            .orElse(true); // Allow access if no subscription found (for testing)
    }
    
    public void incrementUsage(Long userId) {
        getByUserId(userId).ifPresentOrElse(sub -> {
            if (sub.getUsageLimit() != -1) {
                sub.setCurrentUsage(sub.getCurrentUsage() + 1);
                subscriptionRepository.save(sub);
            }
        }, () -> {
            // No subscription found, skip usage tracking for testing
        });
    }
}