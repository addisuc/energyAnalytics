package com.weather.service.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Configuration
@EnableJpaRepositories(basePackages = "com.weather.service.repository")
@EntityScan(basePackages = "com.weather.service.entity")
@EnableTransactionManagement
public class DatabaseOptimizationConfig {

    private final DataSource dataSource;

    public DatabaseOptimizationConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void createIndexes() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            
            // Weather data indexes
            createIndexIfNotExists(statement, 
                "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_weather_data_city_timestamp ON weather_data(city, timestamp DESC)");
            
            createIndexIfNotExists(statement, 
                "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_weather_data_timestamp ON weather_data(timestamp DESC)");
            
            // Energy data indexes
            createIndexIfNotExists(statement, 
                "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_energy_data_region_timestamp ON energy_data(region, timestamp DESC)");
            
            // User and subscription indexes
            createIndexIfNotExists(statement, 
                "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_email ON users(email)");
            
            createIndexIfNotExists(statement, 
                "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_subscriptions_user_id ON subscriptions(user_id)");
            
            // Alert indexes
            createIndexIfNotExists(statement, 
                "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_alerts_user_id_active ON alerts(user_id, is_active)");
            
            createIndexIfNotExists(statement, 
                "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_alert_history_alert_id_timestamp ON alert_history(alert_id, timestamp DESC)");
            
            // Historical data indexes
            createIndexIfNotExists(statement, 
                "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_historical_weather_region_date ON historical_weather_data(region, date DESC)");
            
        } catch (Exception e) {
            // Log but don't fail startup
            System.err.println("Failed to create database indexes: " + e.getMessage());
        }
    }

    private void createIndexIfNotExists(Statement statement, String sql) {
        try {
            statement.execute(sql);
        } catch (Exception e) {
            // Index might already exist or table might not exist yet
            System.out.println("Index creation skipped: " + e.getMessage());
        }
    }
}