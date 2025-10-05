package com.weather.service.repository;

import com.weather.service.entity.WeatherForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Long> {
    
    List<WeatherForecast> findByCityIgnoreCaseAndForecastDateBetweenOrderByForecastDate(
            String city, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT w FROM WeatherForecast w WHERE LOWER(w.city) = LOWER(:city) " +
           "AND w.forecastDate >= :fromDate ORDER BY w.forecastDate ASC")
    List<WeatherForecast> findForecastData(@Param("city") String city, 
                                         @Param("fromDate") LocalDateTime fromDate);
    
    void deleteByCityIgnoreCaseAndCreatedAtBefore(String city, LocalDateTime before);
}