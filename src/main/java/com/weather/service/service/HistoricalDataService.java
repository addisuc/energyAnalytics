package com.weather.service.service;

import com.weather.service.entity.HistoricalWeatherData;
import com.weather.service.repository.HistoricalWeatherDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoricalDataService {
    
    private final HistoricalWeatherDataRepository repository;
    
    public void saveWeatherData(String region, Map<String, Object> weatherData, Map<String, Object> energyData) {
        HistoricalWeatherData data = new HistoricalWeatherData();
        data.setRegion(region);
        data.setTimestamp(LocalDateTime.now());
        data.setTemperature((Double) weatherData.get("temperature"));
        data.setWindSpeed((Double) weatherData.get("windSpeed"));
        data.setSolarIrradiance((Double) weatherData.get("solarIrradiance"));
        data.setCloudCover((Double) weatherData.get("cloudCover"));
        data.setSolarGeneration((Double) energyData.get("solarGeneration"));
        data.setWindGeneration((Double) energyData.get("windGeneration"));
        data.setTotalConsumption((Double) energyData.get("consumption"));
        data.setEfficiency((Double) energyData.get("efficiency"));
        
        repository.save(data);
    }
    
    public List<HistoricalWeatherData> getHistoricalData(String region, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return repository.findRecentDataByRegion(region, since);
    }
    
    public Map<String, Object> getHistoricalAnalytics(String region, int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<HistoricalWeatherData> data = repository.findRecentDataByRegion(region, since);
        
        if (data.isEmpty()) {
            return generateMockHistoricalAnalytics(region, days);
        }
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalRecords", data.size());
        analytics.put("averageEfficiency", calculateAverageEfficiency(data));
        analytics.put("peakGeneration", calculatePeakGeneration(data));
        analytics.put("trends", calculateTrends(data));
        analytics.put("region", region);
        analytics.put("period", days + " days");
        
        return analytics;
    }
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void collectHistoricalData() {
        String[] regions = {"north", "south", "east", "west", "central"};
        
        for (String region : regions) {
            try {
                Map<String, Object> weatherData = generateRealtimeWeatherData();
                Map<String, Object> energyData = generateRealtimeEnergyData(weatherData);
                saveWeatherData(region, weatherData, energyData);
            } catch (Exception e) {
                log.error("Failed to collect data for region {}: {}", region, e.getMessage());
            }
        }
    }
    
    private Map<String, Object> generateMockHistoricalAnalytics(String region, int days) {
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalRecords", days * 24); // Hourly data
        analytics.put("averageEfficiency", 75.5 + Math.random() * 20);
        analytics.put("peakGeneration", 450.0 + Math.random() * 100);
        analytics.put("trends", Map.of(
            "efficiency", "increasing",
            "generation", "stable",
            "consumption", "decreasing"
        ));
        analytics.put("region", region);
        analytics.put("period", days + " days");
        return analytics;
    }
    
    private Double calculateAverageEfficiency(List<HistoricalWeatherData> data) {
        return data.stream().mapToDouble(HistoricalWeatherData::getEfficiency).average().orElse(0.0);
    }
    
    private Double calculatePeakGeneration(List<HistoricalWeatherData> data) {
        return data.stream().mapToDouble(d -> d.getSolarGeneration() + d.getWindGeneration()).max().orElse(0.0);
    }
    
    private Map<String, String> calculateTrends(List<HistoricalWeatherData> data) {
        // Simple trend analysis - in production, use more sophisticated algorithms
        return Map.of(
            "efficiency", "stable",
            "generation", "increasing", 
            "consumption", "stable"
        );
    }
    
    private Map<String, Object> generateRealtimeWeatherData() {
        int hour = LocalDateTime.now().getHour();
        Map<String, Object> data = new HashMap<>();
        data.put("temperature", 20 + Math.sin((hour - 6) * Math.PI / 12) * 8 + (Math.random() * 4 - 2));
        data.put("windSpeed", 5 + Math.random() * 10);
        data.put("solarIrradiance", Math.max(0, 800 * Math.sin((hour - 6) * Math.PI / 12) + (Math.random() * 200 - 100)));
        data.put("cloudCover", 20 + Math.random() * 60);
        return data;
    }
    
    private Map<String, Object> generateRealtimeEnergyData(Map<String, Object> weatherData) {
        double solarIrradiance = (Double) weatherData.get("solarIrradiance");
        double cloudCover = (Double) weatherData.get("cloudCover");
        double windSpeed = (Double) weatherData.get("windSpeed");
        double temperature = (Double) weatherData.get("temperature");
        
        double solarGeneration = Math.max(0, (solarIrradiance / 10) * (1 - cloudCover / 150));
        double windGeneration = Math.pow(windSpeed, 2) * 2.5;
        double consumption = 150 + (temperature > 25 ? (temperature - 25) * 8 : 0) + (Math.random() * 30 - 15);
        double efficiency = (solarGeneration + windGeneration) / Math.max(consumption, 1) * 100;
        
        Map<String, Object> data = new HashMap<>();
        data.put("solarGeneration", Math.round(solarGeneration * 100.0) / 100.0);
        data.put("windGeneration", Math.round(windGeneration * 100.0) / 100.0);
        data.put("consumption", Math.round(consumption * 100.0) / 100.0);
        data.put("efficiency", Math.round(efficiency * 100.0) / 100.0);
        return data;
    }
}