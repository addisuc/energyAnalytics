package com.weather.service.repository;

import com.weather.service.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    
    Optional<WeatherData> findTopByCityIgnoreCaseOrderByTimestampDesc(String city);
    
    List<WeatherData> findByCityIgnoreCaseAndTimestampBetweenOrderByTimestampDesc(
            String city, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT w FROM WeatherData w WHERE LOWER(w.city) = LOWER(:city) " +
           "AND w.timestamp >= :since ORDER BY w.timestamp DESC")
    List<WeatherData> findRecentWeatherData(@Param("city") String city, 
                                          @Param("since") LocalDateTime since);
}