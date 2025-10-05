package com.weather.service.repository;

import com.weather.service.entity.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertHistoryRepository extends JpaRepository<AlertHistory, Long> {
    
    List<AlertHistory> findByAlertIdOrderByTriggeredAtDesc(Long alertId);
    
    List<AlertHistory> findByTriggeredAtBetweenOrderByTriggeredAtDesc(LocalDateTime start, LocalDateTime end);
    
    long countByAlertIdAndTriggeredAtAfter(Long alertId, LocalDateTime since);
}