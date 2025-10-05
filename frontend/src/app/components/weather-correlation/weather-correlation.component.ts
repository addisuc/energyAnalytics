import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil, interval, startWith } from 'rxjs';
import { AnalyticsService } from '../../services/analytics.service';
import { WebSocketService } from '../../services/websocket.service';
import { CorrelationAnalysis, EnergyCorrelation } from '../../models/weather-correlation.model';

@Component({
  selector: 'app-weather-correlation',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './weather-correlation.component.html',
  styleUrl: './weather-correlation.component.scss'
})
export class WeatherCorrelationComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  correlationData: CorrelationAnalysis | null = null;
  selectedRegion = 'california';
  selectedTimeRange = '24h';
  isLoading = false;
  error: string | null = null;
  lastUpdated: Date | null = null;
  
  availableRegions = [
    { value: 'california', label: 'California' },
    { value: 'texas', label: 'Texas' },
    { value: 'newyork', label: 'New York' },
    { value: 'florida', label: 'Florida' },
    { value: 'illinois', label: 'Illinois' },
    { value: 'washington', label: 'Washington' },
    { value: 'oregon', label: 'Oregon' },
    { value: 'arizona', label: 'Arizona' },
    { value: 'nevada', label: 'Nevada' },
    { value: 'colorado', label: 'Colorado' },
    { value: 'northcarolina', label: 'North Carolina' },
    { value: 'georgia', label: 'Georgia' }
  ];
  
  // Expose Math to template
  Math = Math;
  
  timeRanges = [
    { value: '24h', label: 'Last 24 Hours' },
    { value: '7d', label: 'Last 7 Days' },
    { value: '30d', label: 'Last 30 Days' }
  ];

  constructor(
    private analyticsService: AnalyticsService,
    private webSocketService: WebSocketService
  ) {}

  ngOnInit(): void {
    this.loadCorrelationData();
    this.setupAutoRefresh();
    this.setupRealTimeUpdates();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupAutoRefresh(): void {
    interval(60000) // 1 minute
      .pipe(
        startWith(0),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        if (!this.isLoading) {
          this.loadCorrelationData(false);
        }
      });
  }

  private setupRealTimeUpdates(): void {
    this.webSocketService.getWeatherData()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          if (data && this.correlationData) {
            // Update current weather data in real-time
            const currentCorrelation = this.correlationData.correlations[this.correlationData.correlations.length - 1];
            if (currentCorrelation) {
              currentCorrelation.weatherData = {
                ...currentCorrelation.weatherData,
                ...data
              };
            }
          }
        },
        error: (error) => {
          console.error('WebSocket weather data error:', error);
        }
      });
  }

  loadCorrelationData(showLoading: boolean = true): void {
    if (showLoading) {
      this.isLoading = true;
    }
    this.error = null;

    this.analyticsService.getWeatherCorrelation(this.selectedRegion, this.selectedTimeRange)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.correlationData = data;
          this.lastUpdated = new Date();
          this.isLoading = false;
        },
        error: (error) => {
          this.error = error.message;
          this.isLoading = false;
          console.error('Failed to load correlation data:', error);
        }
      });
  }



  onRegionChange(): void {
    this.loadCorrelationData();
  }

  onTimeRangeChange(): void {
    this.loadCorrelationData();
  }

  refreshData(): void {
    this.loadCorrelationData();
  }

  getCorrelationStrength(coefficient: number): string {
    const abs = Math.abs(coefficient);
    if (abs >= 0.9) return 'Very Strong';
    if (abs >= 0.7) return 'Strong';
    if (abs >= 0.5) return 'Moderate';
    if (abs >= 0.3) return 'Weak';
    return 'Very Weak';
  }

  getCorrelationColor(coefficient: number): string {
    const abs = Math.abs(coefficient);
    if (abs >= 0.9) return 'text-green-600';
    if (abs >= 0.7) return 'text-blue-600';
    if (abs >= 0.5) return 'text-yellow-600';
    if (abs >= 0.3) return 'text-orange-600';
    return 'text-red-600';
  }

  getCurrentWeather(): EnergyCorrelation | null {
    return this.correlationData?.correlations[this.correlationData.correlations.length - 1] || null;
  }

  getAverageCorrelation(type: keyof EnergyCorrelation['correlationCoefficients']): number {
    if (!this.correlationData) return 0;
    
    const sum = this.correlationData.correlations.reduce((acc, corr) => 
      acc + corr.correlationCoefficients[type], 0);
    return sum / this.correlationData.correlations.length;
  }

  formatNumber(value: number, decimals: number = 1): string {
    return value.toFixed(decimals);
  }

  getTimeSinceUpdate(): string {
    if (!this.lastUpdated) return 'Never';
    
    const now = new Date();
    const diff = now.getTime() - this.lastUpdated.getTime();
    const seconds = Math.floor(diff / 1000);
    
    if (seconds < 60) return `${seconds}s ago`;
    if (seconds < 3600) return `${Math.floor(seconds / 60)}m ago`;
    return `${Math.floor(seconds / 3600)}h ago`;
  }
}