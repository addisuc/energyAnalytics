package com.weather.service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String region;
    
    @Column(name = "alert_type", nullable = false)
    private String alertType; // DEMAND, GENERATION, PRICE, EFFICIENCY
    
    @Column(name = "threshold_value", nullable = false)
    private Double thresholdValue;
    
    @Column(name = "threshold_operator", nullable = false)
    private String thresholdOperator; // GREATER_THAN, LESS_THAN, EQUALS
    
    @Column(name = "notification_method", nullable = false)
    private String notificationMethod; // EMAIL, SMS, WEBHOOK
    
    @Column(name = "webhook_url")
    private String webhookUrl;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "last_triggered")
    private LocalDateTime lastTriggered;
    
    @Column(name = "trigger_count", nullable = false)
    private Integer triggerCount = 0;
}