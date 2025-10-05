package com.weather.service.repository;

import com.weather.service.entity.EnergyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EnergyDataRepository extends JpaRepository<EnergyData, Long> {
    
    List<EnergyData> findByRegionOrderByTimestampDesc(String region);
    
    @Query("SELECT e FROM EnergyData e WHERE e.region = ?1 AND e.timestamp >= ?2")
    List<EnergyData> findByRegionAndTimestampAfter(String region, LocalDateTime since);
    
    // TODO: Add more complex queries for analytics
}