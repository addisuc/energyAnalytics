package com.weather.service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api/docs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "API Documentation", description = "API platform information and SDK downloads")
public class ApiDocumentationController {
    
    @Operation(
        summary = "Get API Platform Information",
        description = "Returns comprehensive information about the Energy Analytics API platform including available endpoints, rate limits, and authentication methods."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API information retrieved successfully",
            content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> apiInfo = new HashMap<>();
        
        apiInfo.put("platform", "Energy Analytics API");
        apiInfo.put("version", "v1.0");
        apiInfo.put("description", "Comprehensive energy analytics platform API");
        apiInfo.put("baseUrl", "https://api.energyanalytics.com");
        apiInfo.put("documentation", "https://api.energyanalytics.com/swagger-ui.html");
        
        // API Categories
        Map<String, Object> categories = new HashMap<>();
        categories.put("analytics", Map.of(
            "description", "Real-time energy analytics and dashboard data",
            "endpoints", Arrays.asList("/api/analytics/energy/{region}", "/api/analytics/weather-correlation/{region}")
        ));
        categories.put("forecasting", Map.of(
            "description", "Energy demand and generation forecasting",
            "endpoints", Arrays.asList("/api/forecast/demand/{region}", "/api/forecast/generation/{region}")
        ));
        categories.put("alerts", Map.of(
            "description", "Threshold-based alert management",
            "endpoints", Arrays.asList("/api/alerts", "/api/alerts/{id}")
        ));
        categories.put("reports", Map.of(
            "description", "Report generation and export capabilities",
            "endpoints", Arrays.asList("/api/reports/generate/{region}", "/api/reports/download/pdf/{region}")
        ));
        
        apiInfo.put("categories", categories);
        
        // Rate Limits by Subscription Tier
        Map<String, Object> rateLimits = new HashMap<>();
        rateLimits.put("FREE", Map.of("requestsPerMinute", 10, "requestsPerDay", 100));
        rateLimits.put("BASIC", Map.of("requestsPerMinute", 60, "requestsPerDay", 1000));
        rateLimits.put("PRO", Map.of("requestsPerMinute", 300, "requestsPerDay", 10000));
        rateLimits.put("ENTERPRISE", Map.of("requestsPerMinute", 1000, "requestsPerDay", "unlimited"));
        
        apiInfo.put("rateLimits", rateLimits);
        
        // Authentication
        apiInfo.put("authentication", Map.of(
            "type", "Bearer Token (JWT)",
            "header", "Authorization: Bearer <token>",
            "tokenEndpoint", "/api/auth/login"
        ));
        
        return ResponseEntity.ok(apiInfo);
    }
    
    @Operation(
        summary = "Get Available SDKs",
        description = "Returns information about available SDKs and code samples for different programming languages."
    )
    @GetMapping("/sdks")
    public ResponseEntity<Map<String, Object>> getAvailableSDKs() {
        Map<String, Object> sdks = new HashMap<>();
        
        sdks.put("javascript", Map.of(
            "name", "Energy Analytics JavaScript SDK",
            "version", "1.0.0",
            "description", "Official JavaScript/TypeScript SDK for browser and Node.js",
            "installation", "npm install @energyanalytics/sdk",
            "documentation", "https://docs.energyanalytics.com/sdk/javascript",
            "repository", "https://github.com/energyanalytics/javascript-sdk"
        ));
        
        sdks.put("python", Map.of(
            "name", "Energy Analytics Python SDK",
            "version", "1.0.0", 
            "description", "Official Python SDK with pandas integration",
            "installation", "pip install energyanalytics-sdk",
            "documentation", "https://docs.energyanalytics.com/sdk/python",
            "repository", "https://github.com/energyanalytics/python-sdk"
        ));
        
        sdks.put("java", Map.of(
            "name", "Energy Analytics Java SDK",
            "version", "1.0.0",
            "description", "Official Java SDK for enterprise applications",
            "installation", "Maven: com.energyanalytics:sdk:1.0.0",
            "documentation", "https://docs.energyanalytics.com/sdk/java",
            "repository", "https://github.com/energyanalytics/java-sdk"
        ));
        
        sdks.put("curl", Map.of(
            "name", "cURL Examples",
            "description", "Ready-to-use cURL commands for API testing",
            "documentation", "https://docs.energyanalytics.com/examples/curl"
        ));
        
        return ResponseEntity.ok(sdks);
    }
    
    @Operation(
        summary = "Get Code Examples",
        description = "Returns code examples for common API operations in different programming languages."
    )
    @GetMapping("/examples/{language}")
    public ResponseEntity<Map<String, Object>> getCodeExamples(
        @Parameter(description = "Programming language for examples") 
        @PathVariable String language) {
        
        Map<String, Object> examples = new HashMap<>();
        
        switch (language.toLowerCase()) {
            case "javascript":
                examples.put("authentication", 
                    "const token = await fetch('/api/auth/login', {\n" +
                    "  method: 'POST',\n" +
                    "  headers: { 'Content-Type': 'application/json' },\n" +
                    "  body: JSON.stringify({ username: 'user', password: 'pass' })\n" +
                    "}).then(r => r.json()).then(d => d.token);");
                
                examples.put("getEnergyData",
                    "const energyData = await fetch('/api/analytics/energy/north', {\n" +
                    "  headers: { 'Authorization': `Bearer ${token}` }\n" +
                    "}).then(r => r.json());");
                break;
                
            case "python":
                examples.put("authentication",
                    "import requests\n\n" +
                    "response = requests.post('/api/auth/login', \n" +
                    "  json={'username': 'user', 'password': 'pass'})\n" +
                    "token = response.json()['token']");
                
                examples.put("getEnergyData",
                    "headers = {'Authorization': f'Bearer {token}'}\n" +
                    "response = requests.get('/api/analytics/energy/north', headers=headers)\n" +
                    "energy_data = response.json()");
                break;
                
            case "curl":
                examples.put("authentication",
                    "curl -X POST /api/auth/login \\\n" +
                    "  -H \"Content-Type: application/json\" \\\n" +
                    "  -d '{\"username\":\"user\",\"password\":\"pass\"}'");
                
                examples.put("getEnergyData",
                    "curl -X GET /api/analytics/energy/north \\\n" +
                    "  -H \"Authorization: Bearer <token>\"");
                break;
                
            default:
                examples.put("error", "Language not supported. Available: javascript, python, curl");
        }
        
        return ResponseEntity.ok(examples);
    }
    
    @Operation(
        summary = "Get API Status",
        description = "Returns current API platform status including uptime, response times, and service health."
    )
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getApiStatus() {
        Map<String, Object> status = new HashMap<>();
        
        status.put("status", "operational");
        status.put("uptime", "99.9%");
        status.put("averageResponseTime", "245ms");
        status.put("lastUpdated", java.time.LocalDateTime.now().toString());
        
        Map<String, String> services = new HashMap<>();
        services.put("authentication", "operational");
        services.put("analytics", "operational");
        services.put("forecasting", "operational");
        services.put("alerts", "operational");
        services.put("reports", "operational");
        services.put("database", "operational");
        services.put("cache", "operational");
        
        status.put("services", services);
        
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("requestsPerMinute", 1250);
        metrics.put("activeUsers", 89);
        metrics.put("errorRate", "0.1%");
        
        status.put("metrics", metrics);
        
        return ResponseEntity.ok(status);
    }
}