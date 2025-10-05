package com.weather.service.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitoringConfig {

    @Bean
    public Timer apiResponseTimer(MeterRegistry meterRegistry) {
        return Timer.builder("api.response.time")
            .description("API response time")
            .register(meterRegistry);
    }
}