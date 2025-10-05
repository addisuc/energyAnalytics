package com.weather.service.dto;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class WeatherResponse {
    private String city;
    private String country;
    private Double latitude;
    private Double longitude;
    private Double temperature;
    private Double feelsLike;
    private Integer humidity;
    private Double pressure;
    private String description;
    private String icon;
    private Double windSpeed;
    private Integer windDirection;
    private Double visibility;
    private LocalDateTime timestamp;
}