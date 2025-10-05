package com.weather.service.client;

import com.weather.service.dto.OpenWeatherResponse;
import com.weather.service.dto.OpenWeatherForecastResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenWeatherClient {
    
    private final WebClient webClient;
    
    @Value("${openweather.api.key}")
    private String apiKey;
    
    @Value("${openweather.api.url}")
    private String apiUrl;
    
    public Mono<OpenWeatherResponse> getCurrentWeather(String city) {
        return webClient.get()
                .uri(apiUrl + "/weather?q={city}&appid={apiKey}&units=metric&lang=en", city, apiKey)
                .retrieve()
                .bodyToMono(OpenWeatherResponse.class)
                .doOnError(error -> log.error("Error fetching weather for city: {}", city, error));
    }
    
    public Mono<OpenWeatherResponse> getCurrentWeatherByCoordinates(double lat, double lon) {
        return webClient.get()
                .uri(apiUrl + "/weather?lat={lat}&lon={lon}&appid={apiKey}&units=metric&lang=en", lat, lon, apiKey)
                .retrieve()
                .bodyToMono(OpenWeatherResponse.class)
                .doOnError(error -> log.error("Error fetching weather for coordinates: {}, {}", lat, lon, error));
    }
    
    public Mono<OpenWeatherForecastResponse> getForecast(String city) {
        return webClient.get()
                .uri(apiUrl + "/forecast?q={city}&appid={apiKey}&units=metric&lang=en", city, apiKey)
                .retrieve()
                .bodyToMono(OpenWeatherForecastResponse.class)
                .doOnError(error -> log.error("Error fetching forecast for city: {}", city, error));
    }
    
    public Mono<OpenWeatherForecastResponse> getForecastByCoordinates(double lat, double lon) {
        return webClient.get()
                .uri(apiUrl + "/forecast?lat={lat}&lon={lon}&appid={apiKey}&units=metric&lang=en", lat, lon, apiKey)
                .retrieve()
                .bodyToMono(OpenWeatherForecastResponse.class)
                .doOnError(error -> log.error("Error fetching forecast for coordinates: {}, {}", lat, lon, error));
    }
}