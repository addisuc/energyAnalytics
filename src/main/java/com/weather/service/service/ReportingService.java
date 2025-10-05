package com.weather.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportingService {
    
    private final EnergyAnalyticsService analyticsService;
    private final HistoricalDataService historicalDataService;
    private final ForecastingService forecastingService;
    
    public Map<String, Object> generateEnergyReport(String region, String reportType, int days) {
        Map<String, Object> report = new HashMap<>();
        
        try {
            // Report metadata
            report.put("reportId", UUID.randomUUID().toString());
            report.put("reportType", reportType);
            report.put("region", region);
            report.put("period", days + " days");
            report.put("generatedAt", LocalDateTime.now().toString());
            report.put("generatedBy", "Energy Analytics System");
            
            // Current analytics
            Map<String, Object> currentData = analyticsService.getEnergyDashboard(region);
            report.put("currentAnalytics", currentData);
            
            // Historical data
            Map<String, Object> historicalData = historicalDataService.getHistoricalAnalytics(region, days);
            report.put("historicalAnalytics", historicalData);
            
            // Forecasting data
            Map<String, Object> forecastData = forecastingService.getComprehensiveForecast(region, Math.min(days, 14));
            report.put("forecastAnalytics", forecastData);
            
            // Report summary
            report.put("summary", generateReportSummary(currentData, historicalData, forecastData));
            
            // Recommendations
            report.put("recommendations", generateRecommendations(currentData, historicalData, forecastData));
            
            log.info("Generated {} report for region {} covering {} days", reportType, region, days);
            
        } catch (Exception e) {
            log.error("Error generating report: {}", e.getMessage());
            throw new RuntimeException("Failed to generate report: " + e.getMessage());
        }
        
        return report;
    }
    
    public byte[] generatePdfReport(String region, String reportType, int days) {
        try {
            Map<String, Object> reportData = generateEnergyReport(region, reportType, days);
            
            // Mock PDF generation - in production, use iText, Apache PDFBox, etc.
            String pdfContent = generatePdfContent(reportData);
            
            log.info("Generated PDF report: {} bytes", pdfContent.length());
            
            // Return mock PDF content as bytes
            return pdfContent.getBytes();
            
        } catch (Exception e) {
            log.error("Error generating PDF report: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF report: " + e.getMessage());
        }
    }
    
    public byte[] generateExcelReport(String region, String reportType, int days) {
        try {
            Map<String, Object> reportData = generateEnergyReport(region, reportType, days);
            
            // Mock Excel generation - in production, use Apache POI
            String csvContent = generateCsvContent(reportData);
            
            log.info("Generated Excel report: {} bytes", csvContent.length());
            
            // Return CSV content as bytes (mock Excel)
            return csvContent.getBytes();
            
        } catch (Exception e) {
            log.error("Error generating Excel report: {}", e.getMessage());
            throw new RuntimeException("Failed to generate Excel report: " + e.getMessage());
        }
    }
    
    public List<Map<String, Object>> getAvailableReportTemplates() {
        List<Map<String, Object>> templates = new ArrayList<>();
        
        templates.add(Map.of(
            "id", "comprehensive",
            "name", "Comprehensive Energy Report",
            "description", "Complete analysis including current, historical, and forecast data",
            "formats", Arrays.asList("PDF", "Excel"),
            "estimatedTime", "2-3 minutes"
        ));
        
        templates.add(Map.of(
            "id", "historical",
            "name", "Historical Analysis Report",
            "description", "Detailed historical trends and performance analysis",
            "formats", Arrays.asList("PDF", "Excel"),
            "estimatedTime", "1-2 minutes"
        ));
        
        templates.add(Map.of(
            "id", "forecast",
            "name", "Forecasting Report",
            "description", "Predictive analytics and future projections",
            "formats", Arrays.asList("PDF", "Excel"),
            "estimatedTime", "1-2 minutes"
        ));
        
        templates.add(Map.of(
            "id", "executive",
            "name", "Executive Summary",
            "description", "High-level overview for management and stakeholders",
            "formats", Arrays.asList("PDF"),
            "estimatedTime", "1 minute"
        ));
        
        return templates;
    }
    
    public Map<String, Object> scheduleReport(Map<String, Object> scheduleData) {
        Map<String, Object> scheduledReport = new HashMap<>();
        
        scheduledReport.put("scheduleId", UUID.randomUUID().toString());
        scheduledReport.put("reportType", scheduleData.get("reportType"));
        scheduledReport.put("region", scheduleData.get("region"));
        scheduledReport.put("frequency", scheduleData.get("frequency")); // DAILY, WEEKLY, MONTHLY
        scheduledReport.put("format", scheduleData.get("format"));
        scheduledReport.put("recipients", scheduleData.get("recipients"));
        scheduledReport.put("nextRun", calculateNextRun((String) scheduleData.get("frequency")));
        scheduledReport.put("status", "ACTIVE");
        scheduledReport.put("createdAt", LocalDateTime.now().toString());
        
        log.info("Scheduled {} report for region {} with {} frequency", 
            scheduleData.get("reportType"), scheduleData.get("region"), scheduleData.get("frequency"));
        
        return scheduledReport;
    }
    
    private Map<String, Object> generateReportSummary(Map<String, Object> current, 
                                                     Map<String, Object> historical, 
                                                     Map<String, Object> forecast) {
        Map<String, Object> summary = new HashMap<>();
        
        // Current performance
        summary.put("currentEfficiency", current.get("efficiency"));
        summary.put("currentGeneration", current.get("totalGeneration"));
        summary.put("currentConsumption", current.get("consumption"));
        
        // Historical trends
        summary.put("averageHistoricalEfficiency", historical.get("averageEfficiency"));
        summary.put("peakHistoricalGeneration", historical.get("peakGeneration"));
        
        // Forecast insights
        Map<String, Object> forecastSummary = (Map<String, Object>) forecast.get("demandForecast");
        if (forecastSummary != null) {
            Map<String, Object> forecastSummaryData = (Map<String, Object>) forecastSummary.get("summary");
            if (forecastSummaryData != null) {
                summary.put("predictedAverageDemand", forecastSummaryData.get("averageDemand"));
                summary.put("predictedPeakDemand", forecastSummaryData.get("peakDemand"));
            }
        }
        
        // Performance indicators
        summary.put("performanceRating", calculatePerformanceRating(current, historical));
        summary.put("riskLevel", "LOW"); // From forecast risk assessment
        
        return summary;
    }
    
    private List<String> generateRecommendations(Map<String, Object> current, 
                                               Map<String, Object> historical, 
                                               Map<String, Object> forecast) {
        List<String> recommendations = new ArrayList<>();
        
        Double currentEfficiency = (Double) current.get("efficiency");
        Double historicalEfficiency = (Double) historical.get("averageEfficiency");
        
        if (currentEfficiency < historicalEfficiency * 0.9) {
            recommendations.add("Current efficiency is below historical average. Consider optimizing energy distribution.");
        }
        
        if (currentEfficiency > 90) {
            recommendations.add("Excellent efficiency performance. Maintain current operational practices.");
        } else if (currentEfficiency > 75) {
            recommendations.add("Good efficiency levels. Look for opportunities to optimize peak hour performance.");
        } else {
            recommendations.add("Efficiency below optimal levels. Recommend comprehensive system review.");
        }
        
        recommendations.add("Monitor weather patterns for renewable generation optimization.");
        recommendations.add("Consider demand response programs during peak consumption periods.");
        recommendations.add("Implement predictive maintenance based on historical performance data.");
        
        return recommendations;
    }
    
    private String calculatePerformanceRating(Map<String, Object> current, Map<String, Object> historical) {
        Double currentEfficiency = (Double) current.get("efficiency");
        
        if (currentEfficiency >= 90) return "EXCELLENT";
        if (currentEfficiency >= 80) return "GOOD";
        if (currentEfficiency >= 70) return "FAIR";
        return "NEEDS_IMPROVEMENT";
    }
    
    private String generatePdfContent(Map<String, Object> reportData) {
        StringBuilder pdf = new StringBuilder();
        
        pdf.append("ENERGY ANALYTICS REPORT\n");
        pdf.append("======================\n\n");
        pdf.append("Report ID: ").append(reportData.get("reportId")).append("\n");
        pdf.append("Region: ").append(reportData.get("region")).append("\n");
        pdf.append("Period: ").append(reportData.get("period")).append("\n");
        pdf.append("Generated: ").append(reportData.get("generatedAt")).append("\n\n");
        
        pdf.append("EXECUTIVE SUMMARY\n");
        pdf.append("-----------------\n");
        Map<String, Object> summary = (Map<String, Object>) reportData.get("summary");
        if (summary != null) {
            pdf.append("Performance Rating: ").append(summary.get("performanceRating")).append("\n");
            pdf.append("Current Efficiency: ").append(summary.get("currentEfficiency")).append("%\n");
            pdf.append("Risk Level: ").append(summary.get("riskLevel")).append("\n\n");
        }
        
        pdf.append("RECOMMENDATIONS\n");
        pdf.append("---------------\n");
        List<String> recommendations = (List<String>) reportData.get("recommendations");
        if (recommendations != null) {
            for (int i = 0; i < recommendations.size(); i++) {
                pdf.append((i + 1)).append(". ").append(recommendations.get(i)).append("\n");
            }
        }
        
        pdf.append("\n--- End of Report ---");
        
        return pdf.toString();
    }
    
    private String generateCsvContent(Map<String, Object> reportData) {
        StringBuilder csv = new StringBuilder();
        
        csv.append("Report Type,Region,Period,Generated At\n");
        csv.append(reportData.get("reportType")).append(",");
        csv.append(reportData.get("region")).append(",");
        csv.append(reportData.get("period")).append(",");
        csv.append(reportData.get("generatedAt")).append("\n\n");
        
        csv.append("Metric,Current Value,Historical Average,Forecast\n");
        
        Map<String, Object> current = (Map<String, Object>) reportData.get("currentAnalytics");
        Map<String, Object> historical = (Map<String, Object>) reportData.get("historicalAnalytics");
        Map<String, Object> summary = (Map<String, Object>) reportData.get("summary");
        
        if (current != null && historical != null && summary != null) {
            csv.append("Efficiency (%),").append(current.get("efficiency")).append(",")
               .append(historical.get("averageEfficiency")).append(",").append("N/A").append("\n");
            
            csv.append("Generation (MW),").append(current.get("totalGeneration")).append(",")
               .append("N/A").append(",").append(summary.get("predictedAverageDemand")).append("\n");
            
            csv.append("Consumption (MW),").append(current.get("consumption")).append(",")
               .append("N/A").append(",").append(summary.get("predictedPeakDemand")).append("\n");
        }
        
        return csv.toString();
    }
    
    private String calculateNextRun(String frequency) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (frequency.toUpperCase()) {
            case "DAILY":
                return now.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            case "WEEKLY":
                return now.plusWeeks(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            case "MONTHLY":
                return now.plusMonths(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            default:
                return now.plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }
}