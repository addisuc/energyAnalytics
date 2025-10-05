package com.weather.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ErrorTrackingService {

    private final MetricsService metricsService;

    public String trackError(Exception exception, String context, String userId) {
        String errorId = UUID.randomUUID().toString();
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("errorId", errorId);
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("exception", exception.getClass().getSimpleName());
        errorDetails.put("message", exception.getMessage());
        errorDetails.put("context", context);
        errorDetails.put("userId", userId);
        errorDetails.put("stackTrace", getStackTraceString(exception));
        
        // Log structured error
        log.error("APPLICATION_ERROR: {}", errorDetails);
        
        // Record metrics
        metricsService.recordApiCall(context, "ERROR", 500, 0);
        
        // In production, send to error tracking service (Sentry, Rollbar, etc.)
        sendToErrorTrackingService(errorDetails);
        
        return errorId;
    }

    public void trackPerformanceIssue(String operation, long duration, long threshold) {
        if (duration > threshold) {
            Map<String, Object> performanceIssue = new HashMap<>();
            performanceIssue.put("timestamp", LocalDateTime.now().toString());
            performanceIssue.put("operation", operation);
            performanceIssue.put("duration", duration);
            performanceIssue.put("threshold", threshold);
            performanceIssue.put("severity", duration > threshold * 2 ? "HIGH" : "MEDIUM");
            
            log.warn("PERFORMANCE_ISSUE: {}", performanceIssue);
            
            // Record performance metrics
            metricsService.recordBusinessMetric("slow_operations", 1);
        }
    }

    public void trackBusinessError(String errorType, String details, String userId) {
        Map<String, Object> businessError = new HashMap<>();
        businessError.put("timestamp", LocalDateTime.now().toString());
        businessError.put("errorType", errorType);
        businessError.put("details", details);
        businessError.put("userId", userId);
        businessError.put("severity", "BUSINESS");
        
        log.warn("BUSINESS_ERROR: {}", businessError);
        
        // Track business error metrics
        metricsService.recordBusinessMetric("business_errors", 1);
    }

    public void trackApiError(String endpoint, String method, int statusCode, String error) {
        Map<String, Object> apiError = new HashMap<>();
        apiError.put("timestamp", LocalDateTime.now().toString());
        apiError.put("endpoint", endpoint);
        apiError.put("method", method);
        apiError.put("statusCode", statusCode);
        apiError.put("error", error);
        
        log.error("API_ERROR: {}", apiError);
        
        // Record API error metrics
        metricsService.recordApiCall(endpoint, method, statusCode, 0);
    }

    private String getStackTraceString(Exception exception) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : exception.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    private void sendToErrorTrackingService(Map<String, Object> errorDetails) {
        // In production, integrate with:
        // - Sentry: Sentry.captureException(exception)
        // - Rollbar: Rollbar.log(exception)
        // - Custom webhook
        log.debug("Error tracking service integration placeholder: {}", errorDetails.get("errorId"));
    }

    public void trackUptimeEvent(String service, boolean isUp) {
        Map<String, Object> uptimeEvent = new HashMap<>();
        uptimeEvent.put("timestamp", LocalDateTime.now().toString());
        uptimeEvent.put("service", service);
        uptimeEvent.put("status", isUp ? "UP" : "DOWN");
        
        if (!isUp) {
            log.error("SERVICE_DOWN: {}", uptimeEvent);
        } else {
            log.info("SERVICE_UP: {}", uptimeEvent);
        }
        
        // Record uptime metrics
        metricsService.recordBusinessMetric(service + "_uptime", isUp ? 1 : 0);
    }
}