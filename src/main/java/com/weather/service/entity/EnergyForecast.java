package com.weather.service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "energy_forecasts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyForecast {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String region;
    
    @Column(name = "forecast_date", nullable = false)
    private LocalDateTime forecastDate;
    
    @Column(name = "target_date", nullable = false)
    private LocalDateTime targetDate;
    
    @Column(name = "predicted_demand", nullable = false)
    private Double predictedDemand;
    
    @Column(name = "predicted_solar_generation", nullable = false)
    private Double predictedSolarGeneration;
    
    @Column(name = "predicted_wind_generation", nullable = false)
    private Double predictedWindGeneration;
    
    @Column(name = "predicted_price", nullable = false)
    private Double predictedPrice;
    
    @Column(name = "confidence_level", nullable = false)
    private Double confidenceLevel;
    
    @Column(name = "forecast_type", nullable = false)
    private String forecastType; // DEMAND, GENERATION, PRICE
}