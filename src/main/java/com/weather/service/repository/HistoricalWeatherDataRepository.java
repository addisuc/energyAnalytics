package com.weather.service.repository;

import com.weather.service.entity.HistoricalWeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoricalWeatherDataRepository extends JpaRepository<HistoricalWeatherData, Long> {
    
    List<HistoricalWeatherData> findByRegionAndTimestampBetweenOrderByTimestampDesc(
        String region, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT h FROM HistoricalWeatherData h WHERE h.region = :region AND h.timestamp >= :since ORDER BY h.timestamp DESC")
    List<HistoricalWeatherData> findRecentDataByRegion(@Param("region") String region, @Param("since") LocalDateTime since);
    
    @Query("SELECT AVG(h.efficiency) FROM HistoricalWeatherData h WHERE h.region = :region AND h.timestamp >= :since")
    Double getAverageEfficiencyByRegion(@Param("region") String region, @Param("since") LocalDateTime since);
    
    // TimescaleDB time-bucket queries for better performance
    @Query(value = "SELECT time_bucket('1 hour', timestamp) as hour, " +
           "AVG(temperature) as avg_temp, AVG(solar_generation) as avg_solar " +
           "FROM historical_weather_data " +
           "WHERE region = :region AND timestamp >= :since " +
           "GROUP BY hour ORDER BY hour DESC LIMIT 24", nativeQuery = true)
    List<Object[]> findHourlyAverages(@Param("region") String region, @Param("since") LocalDateTime since);
}