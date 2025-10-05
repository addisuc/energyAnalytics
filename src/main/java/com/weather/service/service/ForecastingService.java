package com.weather.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastingService {
    
    private final HistoricalDataService historicalDataService;
    
    public Map<String, Object> generateEnergyDemandForecast(String region, int days) {
        Map<String, Object> forecast = new HashMap<>();
        
        // Generate 7-day hourly forecasts
        List<Map<String, Object>> hourlyForecasts = generateHourlyDemandForecast(region, days);
        List<Map<String, Object>> dailyForecasts = generateDailyDemandForecast(region, days);
        
        forecast.put("region", region);
        forecast.put("forecastPeriod", days + " days");
        forecast.put("generatedAt", LocalDateTime.now().toString());
        forecast.put("hourlyForecasts", hourlyForecasts);
        forecast.put("dailyForecasts", dailyForecasts);
        forecast.put("summary", generateForecastSummary(hourlyForecasts));
        
        return forecast;
    }
    
    public Map<String, Object> generateWeatherBasedGenerationForecast(String region, int days) {
        Map<String, Object> forecast = new HashMap<>();
        
        List<Map<String, Object>> generationForecasts = generateGenerationForecast(region, days);
        
        forecast.put("region", region);
        forecast.put("forecastPeriod", days + " days");
        forecast.put("generatedAt", LocalDateTime.now().toString());
        forecast.put("forecasts", generationForecasts);
        forecast.put("totalPredictedGeneration", calculateTotalGeneration(generationForecasts));
        forecast.put("weatherFactors", generateWeatherFactors());
        
        return forecast;
    }
    
    public Map<String, Object> generatePriceForecast(String region, int days) {
        Map<String, Object> forecast = new HashMap<>();
        
        List<Map<String, Object>> priceForecasts = generatePriceForecastData(region, days);
        
        forecast.put("region", region);
        forecast.put("forecastPeriod", days + " days");
        forecast.put("generatedAt", LocalDateTime.now().toString());
        forecast.put("priceForecasts", priceForecasts);
        forecast.put("priceAnalysis", generatePriceAnalysis(priceForecasts));
        forecast.put("marketFactors", generateMarketFactors());
        
        return forecast;
    }
    
    public Map<String, Object> getComprehensiveForecast(String region, int days) {
        Map<String, Object> comprehensive = new HashMap<>();
        
        comprehensive.put("region", region);
        comprehensive.put("forecastPeriod", days + " days");
        comprehensive.put("generatedAt", LocalDateTime.now().toString());
        comprehensive.put("demandForecast", generateEnergyDemandForecast(region, days));
        comprehensive.put("generationForecast", generateWeatherBasedGenerationForecast(region, days));
        comprehensive.put("priceForecast", generatePriceForecast(region, days));
        comprehensive.put("riskAssessment", generateRiskAssessment(region, days));
        
        return comprehensive;
    }
    
    private List<Map<String, Object>> generateHourlyDemandForecast(String region, int days) {
        List<Map<String, Object>> forecasts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int day = 0; day < days; day++) {
            for (int hour = 0; hour < 24; hour++) {
                LocalDateTime targetTime = now.plusDays(day).withHour(hour).withMinute(0).withSecond(0);
                
                // Simulate demand patterns based on time of day and region
                double baseDemand = getBaseDemandForRegion(region);
                double hourlyMultiplier = getHourlyDemandMultiplier(hour);
                double seasonalFactor = getSeasonalFactor(targetTime);
                double randomVariation = 0.9 + (Math.random() * 0.2); // ±10% variation
                
                double predictedDemand = baseDemand * hourlyMultiplier * seasonalFactor * randomVariation;
                double confidence = 0.75 + (Math.random() * 0.2); // 75-95% confidence
                
                Map<String, Object> hourlyForecast = new HashMap<>();
                hourlyForecast.put("timestamp", targetTime.toString());
                hourlyForecast.put("predictedDemand", Math.round(predictedDemand * 100.0) / 100.0);
                hourlyForecast.put("confidence", Math.round(confidence * 100.0) / 100.0);
                hourlyForecast.put("hour", hour);
                hourlyForecast.put("day", day + 1);
                
                forecasts.add(hourlyForecast);
            }
        }
        
        return forecasts;
    }
    
    private List<Map<String, Object>> generateDailyDemandForecast(String region, int days) {
        List<Map<String, Object>> dailyForecasts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int day = 0; day < days; day++) {
            LocalDateTime targetDate = now.plusDays(day);
            
            double baseDemand = getBaseDemandForRegion(region) * 24; // Daily total
            double seasonalFactor = getSeasonalFactor(targetDate);
            double weekdayFactor = getWeekdayFactor(targetDate);
            double randomVariation = 0.9 + (Math.random() * 0.2);
            
            double predictedDailyDemand = baseDemand * seasonalFactor * weekdayFactor * randomVariation;
            double confidence = 0.80 + (Math.random() * 0.15);
            
            Map<String, Object> dailyForecast = new HashMap<>();
            dailyForecast.put("date", targetDate.toLocalDate().toString());
            dailyForecast.put("predictedDemand", Math.round(predictedDailyDemand * 100.0) / 100.0);
            dailyForecast.put("confidence", Math.round(confidence * 100.0) / 100.0);
            dailyForecast.put("dayOfWeek", targetDate.getDayOfWeek().toString());
            
            dailyForecasts.add(dailyForecast);
        }
        
        return dailyForecasts;
    }
    
    private List<Map<String, Object>> generateGenerationForecast(String region, int days) {
        List<Map<String, Object>> forecasts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int day = 0; day < days; day++) {
            LocalDateTime targetDate = now.plusDays(day);
            
            // Weather-based generation predictions
            double solarCapacity = getSolarCapacityForRegion(region);
            double windCapacity = getWindCapacityForRegion(region);
            
            double predictedSunlight = 6 + (Math.random() * 6); // 6-12 hours
            double predictedWindSpeed = 5 + (Math.random() * 10); // 5-15 m/s
            double cloudCover = Math.random() * 0.7; // 0-70% cloud cover
            
            double solarGeneration = solarCapacity * (predictedSunlight / 12) * (1 - cloudCover);
            double windGeneration = windCapacity * Math.min(predictedWindSpeed / 15, 1.0);
            
            Map<String, Object> forecast = new HashMap<>();
            forecast.put("date", targetDate.toLocalDate().toString());
            forecast.put("predictedSolarGeneration", Math.round(solarGeneration * 100.0) / 100.0);
            forecast.put("predictedWindGeneration", Math.round(windGeneration * 100.0) / 100.0);
            forecast.put("totalGeneration", Math.round((solarGeneration + windGeneration) * 100.0) / 100.0);
            forecast.put("weatherFactors", Map.of(
                "sunlightHours", Math.round(predictedSunlight * 10.0) / 10.0,
                "windSpeed", Math.round(predictedWindSpeed * 10.0) / 10.0,
                "cloudCover", Math.round(cloudCover * 100.0)
            ));
            forecast.put("confidence", 0.70 + (Math.random() * 0.25));
            
            forecasts.add(forecast);
        }
        
        return forecasts;
    }
    
    private List<Map<String, Object>> generatePriceForecastData(String region, int days) {
        List<Map<String, Object>> forecasts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        double basePrice = 45.0 + (Math.random() * 20); // $45-65/MWh base
        
        for (int day = 0; day < days; day++) {
            LocalDateTime targetDate = now.plusDays(day);
            
            double demandFactor = 0.9 + (Math.random() * 0.3); // Demand impact
            double supplyFactor = 0.8 + (Math.random() * 0.4); // Supply impact
            double marketVolatility = 0.95 + (Math.random() * 0.1); // Market volatility
            
            double predictedPrice = basePrice * demandFactor * supplyFactor * marketVolatility;
            double confidence = 0.65 + (Math.random() * 0.25);
            
            Map<String, Object> forecast = new HashMap<>();
            forecast.put("date", targetDate.toLocalDate().toString());
            forecast.put("predictedPrice", Math.round(predictedPrice * 100.0) / 100.0);
            forecast.put("confidence", Math.round(confidence * 100.0) / 100.0);
            forecast.put("priceRange", Map.of(
                "low", Math.round((predictedPrice * 0.85) * 100.0) / 100.0,
                "high", Math.round((predictedPrice * 1.15) * 100.0) / 100.0
            ));
            
            forecasts.add(forecast);
        }
        
        return forecasts;
    }
    
    // Helper methods for realistic forecasting
    private double getBaseDemandForRegion(String region) {
        Map<String, Double> regionDemands = Map.of(
            "north", 180.0, "south", 220.0, "east", 200.0, 
            "west", 190.0, "central", 250.0
        );
        return regionDemands.getOrDefault(region, 200.0);
    }
    
    private double getHourlyDemandMultiplier(int hour) {
        // Peak hours: 8-10 AM and 6-8 PM
        if (hour >= 8 && hour <= 10) return 1.3;
        if (hour >= 18 && hour <= 20) return 1.4;
        if (hour >= 0 && hour <= 6) return 0.7; // Night hours
        return 1.0;
    }
    
    private double getSeasonalFactor(LocalDateTime date) {
        int month = date.getMonthValue();
        // Summer (Jun-Aug) and Winter (Dec-Feb) have higher demand
        if (month >= 6 && month <= 8) return 1.2; // Summer AC
        if (month >= 12 || month <= 2) return 1.15; // Winter heating
        return 1.0;
    }
    
    private double getWeekdayFactor(LocalDateTime date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        return (dayOfWeek <= 5) ? 1.1 : 0.9; // Weekdays vs weekends
    }
    
    private double getSolarCapacityForRegion(String region) {
        Map<String, Double> solarCapacities = Map.of(
            "north", 150.0, "south", 200.0, "east", 180.0,
            "west", 170.0, "central", 220.0
        );
        return solarCapacities.getOrDefault(region, 180.0);
    }
    
    private double getWindCapacityForRegion(String region) {
        Map<String, Double> windCapacities = Map.of(
            "north", 120.0, "south", 100.0, "east", 140.0,
            "west", 160.0, "central", 110.0
        );
        return windCapacities.getOrDefault(region, 130.0);
    }
    
    private Map<String, Object> generateForecastSummary(List<Map<String, Object>> forecasts) {
        double avgDemand = forecasts.stream()
            .mapToDouble(f -> (Double) f.get("predictedDemand"))
            .average().orElse(0.0);
        
        double peakDemand = forecasts.stream()
            .mapToDouble(f -> (Double) f.get("predictedDemand"))
            .max().orElse(0.0);
        
        return Map.of(
            "averageDemand", Math.round(avgDemand * 100.0) / 100.0,
            "peakDemand", Math.round(peakDemand * 100.0) / 100.0,
            "totalForecasts", forecasts.size(),
            "averageConfidence", 0.85
        );
    }
    
    private double calculateTotalGeneration(List<Map<String, Object>> forecasts) {
        return forecasts.stream()
            .mapToDouble(f -> (Double) f.get("totalGeneration"))
            .sum();
    }
    
    private Map<String, Object> generateWeatherFactors() {
        return Map.of(
            "expectedSunnyDays", 4 + (int)(Math.random() * 3),
            "expectedWindyDays", 3 + (int)(Math.random() * 4),
            "averageCloudCover", Math.round((30 + Math.random() * 40) * 10.0) / 10.0
        );
    }
    
    private Map<String, Object> generatePriceAnalysis(List<Map<String, Object>> forecasts) {
        double avgPrice = forecasts.stream()
            .mapToDouble(f -> (Double) f.get("predictedPrice"))
            .average().orElse(0.0);
        
        return Map.of(
            "averagePrice", Math.round(avgPrice * 100.0) / 100.0,
            "priceVolatility", "MODERATE",
            "trend", "STABLE",
            "riskLevel", "LOW"
        );
    }
    
    private Map<String, Object> generateMarketFactors() {
        return Map.of(
            "demandGrowth", "2.3%",
            "renewableShare", "35%",
            "gridStability", "HIGH"
        );
    }
    
    private Map<String, Object> generateRiskAssessment(String region, int days) {
        return Map.of(
            "supplyRisk", "LOW",
            "demandRisk", "MODERATE", 
            "priceRisk", "LOW",
            "weatherRisk", "MODERATE",
            "overallRisk", "LOW-MODERATE"
        );
    }
}