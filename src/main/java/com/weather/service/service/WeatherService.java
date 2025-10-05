package com.weather.service.service;

import com.weather.service.client.OpenWeatherClient;
import com.weather.service.dto.ForecastResponse;
import com.weather.service.dto.OpenWeatherResponse;
import com.weather.service.dto.OpenWeatherForecastResponse;
import com.weather.service.dto.WeatherResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j

public class WeatherService {
    
    private final OpenWeatherClient openWeatherClient;

    
    public WeatherResponse getCurrentWeather(String city) {
        try {
            OpenWeatherResponse apiResponse = openWeatherClient.getCurrentWeather(city).block();
            
            if (apiResponse != null) {
                return mapApiToWeatherResponse(apiResponse);
            }
        } catch (Exception e) {
            log.error("Error fetching current weather for city: {}", city, e);
        }
        
        return null;
    }
    
    public WeatherResponse getCurrentWeatherByCoordinates(double lat, double lon) {
        try {
            OpenWeatherResponse apiResponse = openWeatherClient.getCurrentWeatherByCoordinates(lat, lon).block();
            
            if (apiResponse != null) {
                return mapApiToWeatherResponse(apiResponse);
            }
        } catch (Exception e) {
            log.error("Error fetching weather for coordinates: {}, {}", lat, lon, e);
        }
        
        return null;
    }
    
    public ForecastResponse getForecast(String city) {
        try {
            OpenWeatherForecastResponse apiResponse = openWeatherClient.getForecast(city).block();
            
            if (apiResponse != null && apiResponse.getList() != null) {
                return mapToForecastResponse(apiResponse);
            }
        } catch (Exception e) {
            log.error("Error fetching forecast for city: {}", city, e);
        }
        
        return null;
    }
    
    public List<WeatherResponse> getWeatherHistory(String city, int days) {
        // History not implemented - would require database
        return List.of();
    }
    

    
    private ForecastResponse mapToForecastResponse(OpenWeatherForecastResponse response) {
        List<ForecastResponse.ForecastItem> forecasts = response.getList().stream()
                .map(item -> ForecastResponse.ForecastItem.builder()
                        .dateTime(LocalDateTime.ofEpochSecond(item.getDt(), 0, java.time.ZoneOffset.UTC))
                        .temperature(item.getMain().getTemp())
                        .feelsLike(item.getMain().getFeelsLike())
                        .description(item.getWeather().isEmpty() ? "" : item.getWeather().get(0).getDescription())
                        .icon(item.getWeather().isEmpty() ? "" : item.getWeather().get(0).getIcon())
                        .humidity(item.getMain().getHumidity())
                        .windSpeed(item.getWind() != null ? item.getWind().getSpeed() : 0.0)
                        .windDirection(item.getWind() != null ? item.getWind().getDeg() : 0)
                        .pressure(item.getMain().getPressure().doubleValue())
                        .cloudCover(item.getClouds() != null ? item.getClouds().getAll() : 0)
                        .precipitationProbability(item.getPop() != null ? item.getPop() * 100 : 0.0)
                        .build())
                .collect(Collectors.toList());
        
        return ForecastResponse.builder()
                .city(response.getCity().getName())
                .country(response.getCity().getCountry())
                .forecasts(forecasts)
                .build();
    }
    
    private WeatherResponse mapApiToWeatherResponse(OpenWeatherResponse response) {
        return WeatherResponse.builder()
                .city(response.getName())
                .country(response.getSys().getCountry())
                .latitude(response.getCoord().getLat())
                .longitude(response.getCoord().getLon())
                .temperature(response.getMain().getTemp())
                .feelsLike(response.getMain().getFeelsLike())
                .humidity(response.getMain().getHumidity())
                .pressure(response.getMain().getPressure().doubleValue())
                .description(response.getWeather().isEmpty() ? "" : response.getWeather().get(0).getDescription())
                .icon(response.getWeather().isEmpty() ? "" : response.getWeather().get(0).getIcon())
                .windSpeed(response.getWind() != null ? response.getWind().getSpeed() : 0.0)
                .windDirection(response.getWind() != null ? response.getWind().getDeg() : 0)
                .visibility(response.getVisibility() != null ? response.getVisibility().doubleValue() : null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}