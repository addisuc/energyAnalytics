package com.weather.service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ForecastResponse {
    private String city;
    private String country;
    private List<ForecastItem> forecasts;
    
    @Data
    @Builder
    public static class ForecastItem {
        private LocalDateTime dateTime;
        private Double temperature;
        private Double feelsLike;
        private String description;
        private String icon;
        private Integer humidity;
        private Double windSpeed;
        private Integer windDirection;
        private Double pressure;
        private Integer cloudCover;
        private Double precipitationProbability;
    }
}