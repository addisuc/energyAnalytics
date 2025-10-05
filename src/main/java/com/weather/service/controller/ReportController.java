package com.weather.service.controller;

import com.weather.service.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {
    
    private final ReportingService reportingService;
    
    @GetMapping("/generate/{region}")
    public ResponseEntity<Map<String, Object>> generateReport(
            @PathVariable String region,
            @RequestParam(defaultValue = "comprehensive") String reportType,
            @RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> report = reportingService.generateEnergyReport(region, reportType, days);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", report);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @GetMapping("/download/pdf/{region}")
    public ResponseEntity<byte[]> downloadPdfReport(
            @PathVariable String region,
            @RequestParam(defaultValue = "comprehensive") String reportType,
            @RequestParam(defaultValue = "30") int days) {
        try {
            byte[] pdfContent = reportingService.generatePdfReport(region, reportType, days);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                String.format("energy-report-%s-%s.pdf", region, reportType));
            headers.setContentLength(pdfContent.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
                
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/download/excel/{region}")
    public ResponseEntity<byte[]> downloadExcelReport(
            @PathVariable String region,
            @RequestParam(defaultValue = "comprehensive") String reportType,
            @RequestParam(defaultValue = "30") int days) {
        try {
            byte[] excelContent = reportingService.generateExcelReport(region, reportType, days);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
            headers.setContentDispositionFormData("attachment", 
                String.format("energy-report-%s-%s.csv", region, reportType));
            headers.setContentLength(excelContent.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(excelContent);
                
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/templates")
    public ResponseEntity<Map<String, Object>> getReportTemplates() {
        try {
            List<Map<String, Object>> templates = reportingService.getAvailableReportTemplates();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", templates);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/schedule")
    public ResponseEntity<Map<String, Object>> scheduleReport(@RequestBody Map<String, Object> scheduleData) {
        try {
            Map<String, Object> scheduledReport = reportingService.scheduleReport(scheduleData);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", scheduledReport);
            response.put("message", "Report scheduled successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}