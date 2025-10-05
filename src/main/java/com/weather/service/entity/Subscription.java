package com.weather.service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPlan plan;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "usage_limit")
    private Integer usageLimit;
    
    @Column(name = "current_usage")
    private Integer currentUsage = 0;
    
    @Column(name = "stripe_subscription_id")
    private String stripeSubscriptionId;
    
    public enum SubscriptionPlan {
        FREE(100, 1),
        BASIC(1000, 5), 
        PRO(10000, 20),
        ENTERPRISE(-1, -1);
        
        private final int apiLimit;
        private final int regionLimit;
        
        SubscriptionPlan(int apiLimit, int regionLimit) {
            this.apiLimit = apiLimit;
            this.regionLimit = regionLimit;
        }
        
        public int getApiLimit() { return apiLimit; }
        public int getRegionLimit() { return regionLimit; }
    }
    
    public enum SubscriptionStatus {
        ACTIVE, EXPIRED, CANCELLED, SUSPENDED
    }
}