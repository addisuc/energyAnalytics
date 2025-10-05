package com.weather.service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "weather_forecast")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String city;
    
    private String country;
    private Double latitude;
    private Double longitude;
    private Double temperature;
    private Double minTemperature;
    private Double maxTemperature;
    private Integer humidity;
    private Double pressure;
    private String description;
    private String icon;
    private Double windSpeed;
    private Integer windDirection;
    
    @Column(nullable = false)
    private LocalDateTime forecastDate;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private String source;
}