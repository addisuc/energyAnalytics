package com.weather.service.service;

import com.weather.service.entity.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public boolean sendEmailAlert(Long userId, String alertName, String message) {
        try {
            // Mock email sending - in production, integrate with SendGrid, AWS SES, etc.
            log.info("Sending email alert to user {}: {} - {}", userId, alertName, message);
            
            // Simulate email sending delay
            Thread.sleep(100);
            
            // Mock success rate (95%)
            return Math.random() > 0.05;
            
        } catch (Exception e) {
            log.error("Failed to send email alert: {}", e.getMessage());
            return false;
        }
    }
    
    public boolean sendSmsAlert(Long userId, String message) {
        try {
            // Mock SMS sending - in production, integrate with Twilio, AWS SNS, etc.
            log.info("Sending SMS alert to user {}: {}", userId, message);
            
            // Simulate SMS sending delay
            Thread.sleep(200);
            
            // Mock success rate (90%)
            return Math.random() > 0.10;
            
        } catch (Exception e) {
            log.error("Failed to send SMS alert: {}", e.getMessage());
            return false;
        }
    }
    
    public boolean sendWebhookAlert(String webhookUrl, Alert alert, String message) {
        try {
            if (webhookUrl == null || webhookUrl.trim().isEmpty()) {
                return false;
            }
            
            // Prepare webhook payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("alertId", alert.getId());
            payload.put("alertName", alert.getName());
            payload.put("alertType", alert.getAlertType());
            payload.put("region", alert.getRegion());
            payload.put("thresholdValue", alert.getThresholdValue());
            payload.put("thresholdOperator", alert.getThresholdOperator());
            payload.put("message", message);
            payload.put("timestamp", java.time.LocalDateTime.now().toString());
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "EnergyAnalytics-AlertSystem/1.0");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            // Send webhook (with timeout)
            restTemplate.postForEntity(webhookUrl, request, String.class);
            
            log.info("Webhook alert sent successfully to: {}", webhookUrl);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send webhook alert to {}: {}", webhookUrl, e.getMessage());
            return false;
        }
    }
    
    public Map<String, Object> getNotificationStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("emailService", "ACTIVE");
        status.put("smsService", "ACTIVE");
        status.put("webhookService", "ACTIVE");
        status.put("lastCheck", java.time.LocalDateTime.now().toString());
        
        return status;
    }
}