package com.weather.service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "historical_weather_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricalWeatherData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String region;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false)
    private Double temperature;
    
    @Column(name = "wind_speed", nullable = false)
    private Double windSpeed;
    
    @Column(name = "solar_irradiance", nullable = false)
    private Double solarIrradiance;
    
    @Column(name = "cloud_cover", nullable = false)
    private Double cloudCover;
    
    @Column(name = "solar_generation", nullable = false)
    private Double solarGeneration;
    
    @Column(name = "wind_generation", nullable = false)
    private Double windGeneration;
    
    @Column(name = "total_consumption", nullable = false)
    private Double totalConsumption;
    
    @Column(nullable = false)
    private Double efficiency;
}