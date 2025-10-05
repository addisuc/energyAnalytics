import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WeatherMapComponent } from '../weather-map/weather-map.component';
import { WeatherService } from '../../services/weather.service';
import { AuthService } from '../../services/auth.service';
import { WeatherData } from '../../models/weather.model';

@Component({
  selector: 'app-weather-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, WeatherMapComponent],
  templateUrl: './weather-dashboard.component.html',
  styleUrl: './weather-dashboard.component.scss'
})
export class WeatherDashboardComponent implements OnInit {
  cityInput = '';
  weatherData: WeatherData | null = null;
  errorMessage = '';
  loading = false;
  currentUser = '';

  constructor(
    private weatherService: WeatherService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.authService.user$.subscribe(user => {
      this.currentUser = user || 'weather-user';
    });
  }

  async searchWeather() {
    if (!this.cityInput.trim()) {
      this.errorMessage = 'Please enter a city name';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.weatherData = null;

    try {
      const data = await this.weatherService.getCurrentWeather(this.cityInput).toPromise();
      if (data) {
        this.weatherData = data;
      }
    } catch (error: any) {
      this.errorMessage = `Weather API error: ${error.message || 'Unknown error'}`;
    } finally {
      this.loading = false;
    }
  }

  onEnterKey(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.searchWeather();
    }
  }

  logout() {
    this.authService.logout();
  }

  convertToFahrenheit(celsius: number): number {
    return this.weatherService.convertToFahrenheit(celsius);
  }

  convertWindSpeed(mps: number): number {
    return this.weatherService.convertWindSpeed(mps);
  }

  convertPressure(hpa: number): number {
    return this.weatherService.convertPressure(hpa);
  }

  convertVisibility(meters: number): number {
    return this.weatherService.convertVisibility(meters);
  }

  getWindDirection(degrees: number): string {
    return this.weatherService.getWindDirection(degrees);
  }

  getRandomUVIndex(): number {
    return Math.floor(Math.random() * 11);
  }
}