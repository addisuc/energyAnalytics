import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WeatherData, ForecastResponse } from '../models/weather.model';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class WeatherService {

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.token;
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });
  }

  getCurrentWeather(city: string): Observable<WeatherData> {
    return this.http.get<WeatherData>(
      `${environment.apiUrl}/weather/current/${encodeURIComponent(city)}`,
      { headers: this.getHeaders() }
    );
  }

  getForecast(city: string): Observable<ForecastResponse> {
    return this.http.get<ForecastResponse>(
      `${environment.apiUrl}/weather/forecast/${encodeURIComponent(city)}`,
      { headers: this.getHeaders() }
    );
  }

  convertToFahrenheit(celsius: number): number {
    return Math.round((celsius * 9/5) + 32);
  }

  convertWindSpeed(mps: number): number {
    return Math.round(mps * 2.237 * 10) / 10; // m/s to mph
  }

  convertPressure(hpa: number): number {
    return Math.round(hpa * 0.02953 * 100) / 100; // hPa to inHg
  }

  convertVisibility(meters: number): number {
    return Math.round(meters * 0.000621371 * 10) / 10; // meters to miles
  }

  getWindDirection(degrees: number): string {
    const directions = ['N', 'NNE', 'NE', 'ENE', 'E', 'ESE', 'SE', 'SSE', 
                       'S', 'SSW', 'SW', 'WSW', 'W', 'WNW', 'NW', 'NNW'];
    return directions[Math.round(degrees / 22.5) % 16];
  }
}