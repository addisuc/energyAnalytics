export interface EnergyForecast {
  timestamp: string;
  solarGeneration: number;
  windGeneration: number;
  totalGeneration: number;
  consumption: number;
  netBalance: number;
  confidence: number;
}

export interface WeatherForecast {
  timestamp: string;
  temperature: number;
  windSpeed: number;
  solarIrradiance: number;
  cloudCover: number;
  precipitation: number;
}

export interface PredictiveInsight {
  type: 'opportunity' | 'risk' | 'maintenance' | 'efficiency';
  title: string;
  description: string;
  impact: 'high' | 'medium' | 'low';
  timeframe: string;
  confidence: number;
  actionRequired: boolean;
}

export interface PredictiveAnalytics {
  region: string;
  forecastPeriod: string;
  energyForecasts: EnergyForecast[];
  weatherForecasts: WeatherForecast[];
  insights: PredictiveInsight[];
  summary: {
    averageGeneration: number;
    peakDemandTime: string;
    surplusHours: number;
    deficitHours: number;
    overallEfficiency: number;
  };
}