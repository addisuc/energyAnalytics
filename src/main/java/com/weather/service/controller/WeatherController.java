package com.weather.service.controller;

import com.weather.service.dto.WeatherResponse;
import com.weather.service.dto.ForecastResponse;
import com.weather.service.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Tag(name = "Weather", description = "Weather API endpoints")
@CrossOrigin(origins = "*")
public class WeatherController {
    
    private final WeatherService weatherService;
    
    @GetMapping("/current/{city}")
    @Operation(summary = "Get current weather by city name")
    public ResponseEntity<WeatherResponse> getCurrentWeather(
            @Parameter(description = "City name") @PathVariable String city) {
        try {
            WeatherResponse weather = weatherService.getCurrentWeather(city);
            return weather != null ? ResponseEntity.ok(weather) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/current")
    @Operation(summary = "Get current weather by coordinates")
    public ResponseEntity<WeatherResponse> getCurrentWeatherByCoordinates(
            @Parameter(description = "Latitude") @RequestParam double lat,
            @Parameter(description = "Longitude") @RequestParam double lon) {
        
        WeatherResponse weather = weatherService.getCurrentWeatherByCoordinates(lat, lon);
        return weather != null ? ResponseEntity.ok(weather) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/history/{city}")
    @Operation(summary = "Get weather history for a city")
    public ResponseEntity<List<WeatherResponse>> getWeatherHistory(
            @Parameter(description = "City name") @PathVariable String city,
            @Parameter(description = "Number of days") @RequestParam(defaultValue = "7") int days) {
        
        List<WeatherResponse> history = weatherService.getWeatherHistory(city, days);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check endpoint")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Weather Service is running");
    }
    
    @GetMapping("/forecast/{city}")
    @Operation(summary = "Get 5-day weather forecast by city name")
    public ResponseEntity<ForecastResponse> getForecast(
            @Parameter(description = "City name") @PathVariable String city) {
        try {
            ForecastResponse forecast = weatherService.getForecast(city);
            return forecast != null ? ResponseEntity.ok(forecast) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}