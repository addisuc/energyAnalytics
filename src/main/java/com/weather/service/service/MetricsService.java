package com.weather.service.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, AtomicLong> businessMetrics = new ConcurrentHashMap<>();

    // Metrics will be created on-demand

    public void recordApiCall(String endpoint, String method, int statusCode, long duration) {
        Counter.builder("api.calls.total")
            .tag("endpoint", endpoint)
            .tag("method", method)
            .tag("status", String.valueOf(statusCode))
            .register(meterRegistry)
            .increment();

        Timer.Sample sample = Timer.start(meterRegistry);
        sample.stop(Timer.builder("api.request.duration")
            .tag("endpoint", endpoint)
            .tag("method", method)
            .register(meterRegistry));

        if (statusCode >= 400) {
            Counter.builder("api.errors.total")
                .tag("endpoint", endpoint)
                .tag("status", String.valueOf(statusCode))
                .register(meterRegistry)
                .increment();
        }
    }

    public void recordDatabaseQuery(String operation, long duration) {
        Timer.builder("database.query.time")
            .tag("operation", operation)
            .register(meterRegistry)
            .record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public void recordUserRegistration() {
        Counter.builder("users.registrations.total")
            .description("Total user registrations")
            .register(meterRegistry)
            .increment();
        incrementBusinessMetric("active_users");
    }

    public void recordSubscriptionChange(String plan, String action) {
        Counter.builder("subscriptions.changes")
            .tag("plan", plan)
            .tag("action", action)
            .register(meterRegistry)
            .increment();
    }

    public void recordCacheHit(String cacheType) {
        Counter.builder("cache.hits")
            .tag("type", cacheType)
            .register(meterRegistry)
            .increment();
    }

    public void recordCacheMiss(String cacheType) {
        Counter.builder("cache.misses")
            .tag("type", cacheType)
            .register(meterRegistry)
            .increment();
    }

    public void recordBusinessMetric(String metricName, double value) {
        businessMetrics.put(metricName, new AtomicLong((long) value));
    }

    private void incrementBusinessMetric(String metricName) {
        businessMetrics.computeIfAbsent(metricName, k -> new AtomicLong(0)).incrementAndGet();
    }

    public void recordSecurityEvent(String eventType, String severity) {
        Counter.builder("security.events")
            .tag("type", eventType)
            .tag("severity", severity)
            .register(meterRegistry)
            .increment();
    }

    public void recordExternalApiCall(String service, boolean success, long duration) {
        Counter.builder("external.api.calls")
            .tag("service", service)
            .tag("success", String.valueOf(success))
            .register(meterRegistry)
            .increment();

        Timer.builder("external.api.duration")
            .tag("service", service)
            .register(meterRegistry)
            .record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
}