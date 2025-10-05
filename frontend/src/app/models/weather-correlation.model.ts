export interface WeatherData {
  temperature: number;
  humidity: number;
  windSpeed: number;
  solarIrradiance: number;
  cloudCover: number;
  timestamp: string;
}

export interface EnergyCorrelation {
  weatherData: WeatherData;
  solarGeneration: number;
  windGeneration: number;
  totalConsumption: number;
  correlationCoefficients: {
    temperatureVsConsumption: number;
    windSpeedVsWindGeneration: number;
    solarIrradianceVsSolarGeneration: number;
    cloudCoverVsSolarGeneration: number;
  };
}

export interface CorrelationAnalysis {
  region: string;
  timeRange: string;
  correlations: EnergyCorrelation[];
  summary: {
    strongestCorrelation: string;
    weakestCorrelation: string;
    averageEfficiency: number;
  };
}