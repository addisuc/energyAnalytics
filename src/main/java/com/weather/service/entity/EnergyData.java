package com.weather.service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "energy_data")
@Data
public class EnergyData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String region;
    
    @Column(name = "solar_generation")
    private Double solarGeneration; // MW
    
    @Column(name = "wind_generation") 
    private Double windGeneration; // MW
    
    @Column(name = "total_consumption")
    private Double totalConsumption; // MW
    
    @Column(name = "grid_demand")
    private Double gridDemand; // MW
    
    @Column(name = "energy_price")
    private Double energyPrice; // $/MWh
    
    // TODO: This should be more granular - hourly data needed
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    // Missing: Data validation, proper indexing
    // Known issue: No relationship to weather data yet
}