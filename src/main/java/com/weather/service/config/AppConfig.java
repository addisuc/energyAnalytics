package com.weather.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfig {
    
    private Security security = new Security();
    private Data data = new Data();
    
    @lombok.Data
    public static class Security {
        private boolean enabled = true;
    }
    
    @lombok.Data
    public static class Data {
        private boolean initialize = false;
    }
}