package com.weather.service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AuditLoggingService {

    public void logSecurityEvent(SecurityEventType eventType, String userId, String details) {
        Map<String, Object> auditLog = new HashMap<>();
        auditLog.put("timestamp", LocalDateTime.now().toString());
        auditLog.put("eventType", eventType.name());
        auditLog.put("userId", userId);
        auditLog.put("details", details);
        auditLog.put("severity", eventType.getSeverity());
        
        log.info("SECURITY_EVENT: {}", auditLog);
        
        if (eventType.getSeverity().equals("HIGH") || eventType.getSeverity().equals("CRITICAL")) {
            log.warn("HIGH SEVERITY SECURITY EVENT: {}", auditLog);
        }
    }

    public void logApiAccess(String userId, String endpoint, String method, int statusCode) {
        Map<String, Object> auditLog = new HashMap<>();
        auditLog.put("timestamp", LocalDateTime.now().toString());
        auditLog.put("eventType", "API_ACCESS");
        auditLog.put("userId", userId);
        auditLog.put("endpoint", endpoint);
        auditLog.put("method", method);
        auditLog.put("statusCode", statusCode);
        
        log.info("API_ACCESS: {}", auditLog);
    }

    public void logGDPREvent(String userId, GDPREventType eventType, String details) {
        Map<String, Object> auditLog = new HashMap<>();
        auditLog.put("timestamp", LocalDateTime.now().toString());
        auditLog.put("eventType", "GDPR_" + eventType.name());
        auditLog.put("userId", userId);
        auditLog.put("details", details);
        auditLog.put("gdprCompliance", true);
        
        log.info("GDPR_EVENT: {}", auditLog);
    }

    public enum SecurityEventType {
        LOGIN_SUCCESS("INFO"),
        LOGIN_FAILURE("MEDIUM"),
        MULTIPLE_LOGIN_FAILURES("HIGH"),
        UNAUTHORIZED_ACCESS("HIGH"),
        SUSPICIOUS_ACTIVITY("HIGH"),
        DATA_BREACH_ATTEMPT("CRITICAL"),
        API_ABUSE("MEDIUM"),
        RATE_LIMIT_EXCEEDED("MEDIUM");

        private final String severity;

        SecurityEventType(String severity) {
            this.severity = severity;
        }

        public String getSeverity() {
            return severity;
        }
    }

    public enum GDPREventType {
        DATA_CONSENT_GIVEN,
        DATA_CONSENT_WITHDRAWN,
        DATA_ACCESS_REQUEST,
        DATA_DELETION_REQUEST,
        DATA_PORTABILITY_REQUEST
    }
}