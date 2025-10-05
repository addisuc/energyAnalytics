package com.weather.service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "weather_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
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
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    private String source;
}