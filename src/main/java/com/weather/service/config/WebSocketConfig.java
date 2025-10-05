package com.weather.service.config;

import com.weather.service.websocket.EnergyDataWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final EnergyDataWebSocketHandler energyDataWebSocketHandler;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(energyDataWebSocketHandler, "/ws/energy-data")
                .setAllowedOrigins("http://localhost:4200", "http://localhost:3000")
                .withSockJS();
    }
}