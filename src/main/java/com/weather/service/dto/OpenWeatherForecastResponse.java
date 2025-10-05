package com.weather.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenWeatherForecastResponse {
    private String cod;
    private Integer message;
    private Integer cnt;
    private List<ForecastItem> list;
    private City city;
    
    @Data
    public static class ForecastItem {
        private Long dt;
        private Main main;
        private List<Weather> weather;
        private Clouds clouds;
        private Wind wind;
        private Integer visibility;
        private Double pop; // Probability of precipitation
        private Sys sys;
        
        @JsonProperty("dt_txt")
        private String dtTxt;
    }
    
    @Data
    public static class Main {
        private Double temp;
        
        @JsonProperty("feels_like")
        private Double feelsLike;
        
        @JsonProperty("temp_min")
        private Double tempMin;
        
        @JsonProperty("temp_max")
        private Double tempMax;
        
        private Integer pressure;
        
        @JsonProperty("sea_level")
        private Integer seaLevel;
        
        @JsonProperty("grnd_level")
        private Integer grndLevel;
        
        private Integer humidity;
        
        @JsonProperty("temp_kf")
        private Double tempKf;
    }
    
    @Data
    public static class Weather {
        private Integer id;
        private String main;
        private String description;
        private String icon;
    }
    
    @Data
    public static class Clouds {
        private Integer all;
    }
    
    @Data
    public static class Wind {
        private Double speed;
        private Integer deg;
        private Double gust;
    }
    
    @Data
    public static class Sys {
        private String pod;
    }
    
    @Data
    public static class City {
        private Integer id;
        private String name;
        private Coord coord;
        private String country;
        private Integer population;
        private Integer timezone;
        private Long sunrise;
        private Long sunset;
    }
    
    @Data
    public static class Coord {
        private Double lat;
        private Double lon;
    }
}