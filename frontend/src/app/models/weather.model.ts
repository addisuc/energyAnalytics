export interface WeatherData {
  city: string;
  country?: string;
  latitude?: number;
  longitude?: number;
  temperature: number;
  feelsLike: number;
  humidity: number;
  pressure: number;
  description: string;
  icon?: string;
  windSpeed: number;
  windDirection: number;
  visibility?: number;
  timestamp?: string;
}

export interface ForecastItem {
  dateTime: string;
  temperature: number;
  feelsLike: number;
  description: string;
  icon: string;
  humidity: number;
  windSpeed: number;
  windDirection: number;
  pressure: number;
  cloudCover: number;
  precipitationProbability: number;
}

export interface ForecastResponse {
  city: string;
  country: string;
  forecasts: ForecastItem[];
}

export interface AuthRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  success: boolean;
  token: string;
  username: string;
  user: {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    role: string;
    subscriptionPlan: string;
    companyName?: string;
    jobTitle?: string;
  };
  message?: string;
}

export interface City {
  name: string;
  lat: number;
  lng: number;
  state: string;
}