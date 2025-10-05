package com.weather.service.service;

import com.weather.service.entity.Alert;
import com.weather.service.entity.AlertHistory;
import com.weather.service.repository.AlertRepository;
import com.weather.service.repository.AlertHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {
    
    private final AlertRepository alertRepository;
    private final AlertHistoryRepository alertHistoryRepository;
    private final NotificationService notificationService;
    private final EnergyAnalyticsService energyAnalyticsService;
    
    public Alert createAlert(Long userId, Map<String, Object> alertData) {
        Alert alert = new Alert();
        alert.setUserId(userId);
        alert.setName((String) alertData.get("name"));
        alert.setRegion((String) alertData.get("region"));
        alert.setAlertType((String) alertData.get("alertType"));
        alert.setThresholdValue(Double.valueOf(alertData.get("thresholdValue").toString()));
        alert.setThresholdOperator((String) alertData.get("thresholdOperator"));
        alert.setNotificationMethod((String) alertData.get("notificationMethod"));
        alert.setWebhookUrl((String) alertData.get("webhookUrl"));
        alert.setCreatedAt(LocalDateTime.now());
        alert.setIsActive(true);
        
        return alertRepository.save(alert);
    }
    
    public List<Alert> getUserAlerts(Long userId) {
        return alertRepository.findByUserIdAndIsActiveTrue(userId);
    }
    
    public Alert updateAlert(Long alertId, Map<String, Object> alertData) {
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
        
        alert.setName((String) alertData.get("name"));
        alert.setThresholdValue(Double.valueOf(alertData.get("thresholdValue").toString()));
        alert.setThresholdOperator((String) alertData.get("thresholdOperator"));
        alert.setNotificationMethod((String) alertData.get("notificationMethod"));
        alert.setWebhookUrl((String) alertData.get("webhookUrl"));
        alert.setIsActive((Boolean) alertData.get("isActive"));
        
        return alertRepository.save(alert);
    }
    
    public void deleteAlert(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found"));
        alert.setIsActive(false);
        alertRepository.save(alert);
    }
    
    public List<AlertHistory> getAlertHistory(Long alertId) {
        return alertHistoryRepository.findByAlertIdOrderByTriggeredAtDesc(alertId);
    }
    
    public Map<String, Object> getAlertStatistics(Long userId) {
        List<Alert> userAlerts = getUserAlerts(userId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAlerts", userAlerts.size());
        stats.put("activeAlerts", userAlerts.stream().mapToInt(a -> a.getIsActive() ? 1 : 0).sum());
        stats.put("alertsByType", getAlertsByType(userAlerts));
        stats.put("recentTriggers", getRecentTriggers(userAlerts));
        
        return stats;
    }
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void checkAlerts() {
        List<Alert> activeAlerts = alertRepository.findByIsActiveTrue();
        
        for (Alert alert : activeAlerts) {
            try {
                checkSingleAlert(alert);
            } catch (Exception e) {
                log.error("Error checking alert {}: {}", alert.getId(), e.getMessage());
            }
        }
    }
    
    private void checkSingleAlert(Alert alert) {
        // Get current data for the alert's region and type
        Map<String, Object> currentData = getCurrentDataForAlert(alert);
        
        if (currentData == null) {
            return;
        }
        
        Double currentValue = (Double) currentData.get("value");
        if (currentValue == null) {
            return;
        }
        
        boolean shouldTrigger = evaluateThreshold(currentValue, alert.getThresholdValue(), alert.getThresholdOperator());
        
        if (shouldTrigger) {
            triggerAlert(alert, currentValue);
        }
    }
    
    private Map<String, Object> getCurrentDataForAlert(Alert alert) {
        try {
            // Get current analytics data for the region
            Map<String, Object> analytics = energyAnalyticsService.getEnergyDashboard(alert.getRegion());
            
            switch (alert.getAlertType()) {
                case "DEMAND":
                    return Map.of("value", (Double) analytics.get("consumption"));
                case "GENERATION":
                    return Map.of("value", (Double) analytics.get("totalGeneration"));
                case "EFFICIENCY":
                    return Map.of("value", (Double) analytics.get("efficiency"));
                case "PRICE":
                    // Mock price data for now
                    return Map.of("value", 45.0 + Math.random() * 20);
                default:
                    return null;
            }
        } catch (Exception e) {
            log.error("Error getting current data for alert {}: {}", alert.getId(), e.getMessage());
            return null;
        }
    }
    
    private boolean evaluateThreshold(Double currentValue, Double thresholdValue, String operator) {
        switch (operator) {
            case "GREATER_THAN":
                return currentValue > thresholdValue;
            case "LESS_THAN":
                return currentValue < thresholdValue;
            case "EQUALS":
                return Math.abs(currentValue - thresholdValue) < 0.01;
            default:
                return false;
        }
    }
    
    private void triggerAlert(Alert alert, Double currentValue) {
        // Check if alert was recently triggered (avoid spam)
        if (alert.getLastTriggered() != null && 
            alert.getLastTriggered().isAfter(LocalDateTime.now().minusMinutes(30))) {
            return;
        }
        
        // Create alert history record
        AlertHistory history = new AlertHistory();
        history.setAlertId(alert.getId());
        history.setTriggeredAt(LocalDateTime.now());
        history.setActualValue(currentValue);
        history.setThresholdValue(alert.getThresholdValue());
        history.setMessage(generateAlertMessage(alert, currentValue));
        
        // Send notification
        boolean notificationSent = sendNotification(alert, history.getMessage());
        history.setNotificationSent(notificationSent);
        history.setNotificationStatus(notificationSent ? "SENT" : "FAILED");
        
        alertHistoryRepository.save(history);
        
        // Update alert
        alert.setLastTriggered(LocalDateTime.now());
        alert.setTriggerCount(alert.getTriggerCount() + 1);
        alertRepository.save(alert);
        
        log.info("Alert {} triggered for user {}: {}", alert.getId(), alert.getUserId(), history.getMessage());
    }
    
    private String generateAlertMessage(Alert alert, Double currentValue) {
        return String.format("Alert '%s': %s in %s region is %.2f (threshold: %.2f %s)", 
            alert.getName(),
            alert.getAlertType().toLowerCase(),
            alert.getRegion(),
            currentValue,
            alert.getThresholdValue(),
            alert.getThresholdOperator().replace("_", " ").toLowerCase()
        );
    }
    
    private boolean sendNotification(Alert alert, String message) {
        try {
            switch (alert.getNotificationMethod()) {
                case "EMAIL":
                    return notificationService.sendEmailAlert(alert.getUserId(), alert.getName(), message);
                case "SMS":
                    return notificationService.sendSmsAlert(alert.getUserId(), message);
                case "WEBHOOK":
                    return notificationService.sendWebhookAlert(alert.getWebhookUrl(), alert, message);
                default:
                    return false;
            }
        } catch (Exception e) {
            log.error("Error sending notification for alert {}: {}", alert.getId(), e.getMessage());
            return false;
        }
    }
    
    private Map<String, Long> getAlertsByType(List<Alert> alerts) {
        Map<String, Long> byType = new HashMap<>();
        alerts.forEach(alert -> 
            byType.merge(alert.getAlertType(), 1L, Long::sum)
        );
        return byType;
    }
    
    private List<Map<String, Object>> getRecentTriggers(List<Alert> alerts) {
        List<Map<String, Object>> recentTriggers = new ArrayList<>();
        
        for (Alert alert : alerts) {
            if (alert.getLastTriggered() != null && 
                alert.getLastTriggered().isAfter(LocalDateTime.now().minusDays(7))) {
                
                Map<String, Object> trigger = new HashMap<>();
                trigger.put("alertName", alert.getName());
                trigger.put("triggeredAt", alert.getLastTriggered().toString());
                trigger.put("alertType", alert.getAlertType());
                trigger.put("region", alert.getRegion());
                
                recentTriggers.add(trigger);
            }
        }
        
        return recentTriggers;
    }
}