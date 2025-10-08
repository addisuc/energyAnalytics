package com.weather.service.config;

import com.weather.service.entity.EnergyData;
import com.weather.service.entity.HistoricalWeatherData;
import com.weather.service.entity.User;
import com.weather.service.entity.Subscription;
import com.weather.service.repository.EnergyDataRepository;
import com.weather.service.repository.HistoricalWeatherDataRepository;
import com.weather.service.repository.UserRepository;
import com.weather.service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final EnergyDataRepository energyDataRepository;
    private final HistoricalWeatherDataRepository historicalWeatherDataRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AppConfig appConfig;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        
        // Always create demo user
        initializeDemoUser();
        
        // Always initialize sample data for demo
        if (energyDataRepository.count() == 0) {
            log.info("Initializing sample energy data...");
            initializeEnergyData();
        }
        
        if (historicalWeatherDataRepository.count() == 0) {
            log.info("Initializing sample weather data...");
            initializeWeatherData();
        }
        
        log.info("Data initialization completed");
    }
    
    private void initializeDemoUser() {
        try {
            if (userRepository.findByEmail("demo@weather.com").isEmpty()) {
                User demoUser = new User();
                demoUser.setUsername("demo");
                demoUser.setEmail("demo@weather.com");
                demoUser.setPassword("$2a$10$N9qo8uLOickgx2ZMRZoMye/Eo9hfBVV2AfVufF7mXSuHxspnsgTzu"); // BCrypt hash for "demo"
                demoUser.setFirstName("Demo");
                demoUser.setLastName("User");
                demoUser.setRole("USER");
                demoUser.setSubscriptionPlan("PRO");
                demoUser = userRepository.save(demoUser);
                
                // Create PRO subscription
                Subscription subscription = new Subscription();
                subscription.setUser(demoUser);
                subscription.setPlan(Subscription.SubscriptionPlan.PRO);
                subscription.setStatus(Subscription.SubscriptionStatus.ACTIVE);
                subscription.setStartDate(LocalDateTime.now());
                subscription.setUsageLimit(-1); // Unlimited for PRO
                subscription.setCurrentUsage(0);
                subscriptionRepository.save(subscription);
                
                log.info("Demo user created with PRO subscription");
            } else {
                log.info("Demo user already exists");
            }
        } catch (Exception e) {
            log.error("Failed to create demo user: {}", e.getMessage());
        }
    }
    
    private void initializeEnergyData() {
        List<String> regions = Arrays.asList("north", "south", "east", "west", "central");
        LocalDateTime now = LocalDateTime.now();
        
        for (String region : regions) {
            for (int i = 0; i < 24; i++) {
                EnergyData data = new EnergyData();
                data.setRegion(region);
                data.setTimestamp(now.minusHours(i));
                data.setSolarGeneration(Math.random() * 100);
                data.setWindGeneration(Math.random() * 150);
                data.setGridDemand(200 + Math.random() * 100);
                data.setTotalConsumption(180 + Math.random() * 80);
                data.setEnergyPrice(0.08 + Math.random() * 0.04);
                
                energyDataRepository.save(data);
            }
        }
        
        log.info("Created {} energy data records", energyDataRepository.count());
    }
    
    private void initializeWeatherData() {
        List<String> regions = Arrays.asList("north", "south", "east", "west", "central");
        LocalDateTime now = LocalDateTime.now();
        
        for (String region : regions) {
            for (int i = 0; i < 24; i++) {
                HistoricalWeatherData data = new HistoricalWeatherData();
                data.setRegion(region);
                data.setTimestamp(now.minusHours(i));
                data.setTemperature(15 + Math.random() * 20);
                data.setWindSpeed(Math.random() * 15);
                data.setSolarIrradiance(Math.max(0, 800 * Math.sin((i / 24.0) * Math.PI)));
                data.setCloudCover(Math.random() * 100);
                data.setSolarGeneration(Math.random() * 100);
                data.setWindGeneration(Math.random() * 150);
                data.setTotalConsumption(200 + Math.random() * 100);
                data.setEfficiency(60 + Math.random() * 30);
                
                historicalWeatherDataRepository.save(data);
            }
        }
        
        log.info("Created {} weather data records", historicalWeatherDataRepository.count());
    }
}