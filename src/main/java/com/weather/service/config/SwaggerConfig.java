package com.weather.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI energyAnalyticsOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Energy Analytics API")
                .description("Comprehensive energy analytics platform providing weather-correlated insights, forecasting, and risk management for energy professionals.")
                .version("v1.0")
                .contact(new Contact()
                    .name("Energy Analytics Team")
                    .email("api@energyanalytics.com")
                    .url("https://energyanalytics.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Development server"),
                new Server()
                    .url("https://api.energyanalytics.com")
                    .description("Production server")))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT token for API authentication")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}