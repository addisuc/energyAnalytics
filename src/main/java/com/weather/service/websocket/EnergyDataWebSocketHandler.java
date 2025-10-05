package com.weather.service.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnergyDataWebSocketHandler implements WebSocketHandler {
    
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.info("WebSocket connection established: {}", session.getId());
        startRealTimeUpdates(session);
    }
    
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("Received message from {}: {}", session.getId(), message.getPayload());
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Transport error for session {}: {}", session.getId(), exception.getMessage());
        sessions.remove(session.getId());
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session.getId());
        log.info("WebSocket connection closed: {} - {}", session.getId(), closeStatus);
    }
    
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
    private void startRealTimeUpdates(WebSocketSession session) {
        scheduler.scheduleAtFixedRate(() -> {
            if (session.isOpen()) {
                try {
                    Map<String, Object> energyUpdate = generateEnergyUpdate();
                    sendMessage(session, "ENERGY_UPDATE", energyUpdate);
                    
                    Map<String, Object> weatherUpdate = generateWeatherUpdate();
                    sendMessage(session, "WEATHER_UPDATE", weatherUpdate);
                } catch (Exception e) {
                    log.error("Error sending update to session {}: {}", session.getId(), e.getMessage());
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
    
    private Map<String, Object> generateEnergyUpdate() {
        Map<String, Object> weatherData = generateWeatherData();
        Map<String, Object> data = new ConcurrentHashMap<>();
        
        // Correlated solar generation based on irradiance and cloud cover
        double solarIrradiance = (Double) weatherData.get("solarIrradiance");
        double cloudCover = (Double) weatherData.get("cloudCover");
        double solarGeneration = Math.max(0, (solarIrradiance / 10) * (1 - cloudCover / 150));
        
        // Correlated wind generation based on wind speed
        double windSpeed = (Double) weatherData.get("windSpeed");
        double windGeneration = Math.pow(windSpeed, 2) * 2.5;
        
        double totalGeneration = solarGeneration + windGeneration;
        
        // Temperature-correlated consumption (AC usage)
        double temperature = (Double) weatherData.get("temperature");
        double baseConsumption = 150;
        double tempAdjustment = temperature > 25 ? (temperature - 25) * 8 : 0;
        double consumption = baseConsumption + tempAdjustment + (Math.random() * 30 - 15);
        
        data.put("timestamp", java.time.LocalDateTime.now().toString());
        data.put("solarGeneration", Math.round(solarGeneration * 100.0) / 100.0);
        data.put("windGeneration", Math.round(windGeneration * 100.0) / 100.0);
        data.put("totalGeneration", Math.round(totalGeneration * 100.0) / 100.0);
        data.put("consumption", Math.round(consumption * 100.0) / 100.0);
        data.put("efficiency", Math.round((totalGeneration / Math.max(consumption, 1) * 100) * 100.0) / 100.0);
        
        return data;
    }
    
    private Map<String, Object> generateWeatherUpdate() {
        return generateWeatherData();
    }
    
    private Map<String, Object> generateWeatherData() {
        Map<String, Object> data = new ConcurrentHashMap<>();
        int hour = java.time.LocalDateTime.now().getHour();
        
        // Realistic daily patterns
        double temperature = 20 + Math.sin((hour - 6) * Math.PI / 12) * 8 + (Math.random() * 4 - 2);
        double windSpeed = 5 + Math.random() * 10;
        double solarIrradiance = Math.max(0, 800 * Math.sin((hour - 6) * Math.PI / 12) + (Math.random() * 200 - 100));
        double cloudCover = 20 + Math.random() * 60;
        
        data.put("timestamp", java.time.LocalDateTime.now().toString());
        data.put("temperature", Math.round(temperature * 100.0) / 100.0);
        data.put("windSpeed", Math.round(windSpeed * 100.0) / 100.0);
        data.put("solarIrradiance", Math.round(solarIrradiance * 100.0) / 100.0);
        data.put("cloudCover", Math.round(cloudCover * 100.0) / 100.0);
        
        return data;
    }
    
    private void sendMessage(WebSocketSession session, String type, Object payload) throws IOException {
        Map<String, Object> message = new ConcurrentHashMap<>();
        message.put("type", type);
        message.put("payload", payload);
        message.put("timestamp", System.currentTimeMillis());
        
        String jsonMessage = objectMapper.writeValueAsString(message);
        session.sendMessage(new TextMessage(jsonMessage));
    }
}