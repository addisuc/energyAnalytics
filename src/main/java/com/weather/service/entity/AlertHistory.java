package com.weather.service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "alert_id", nullable = false)
    private Long alertId;
    
    @Column(name = "triggered_at", nullable = false)
    private LocalDateTime triggeredAt;
    
    @Column(name = "actual_value", nullable = false)
    private Double actualValue;
    
    @Column(name = "threshold_value", nullable = false)
    private Double thresholdValue;
    
    @Column(nullable = false)
    private String message;
    
    @Column(name = "notification_sent", nullable = false)
    private Boolean notificationSent = false;
    
    @Column(name = "notification_status")
    private String notificationStatus;
}