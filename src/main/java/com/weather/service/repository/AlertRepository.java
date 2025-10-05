package com.weather.service.repository;

import com.weather.service.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    List<Alert> findByUserIdAndIsActiveTrue(Long userId);
    
    List<Alert> findByRegionAndIsActiveTrue(String region);
    
    List<Alert> findByAlertTypeAndIsActiveTrue(String alertType);
    
    List<Alert> findByIsActiveTrue();
}