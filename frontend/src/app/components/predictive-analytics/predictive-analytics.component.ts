import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil, interval, startWith } from 'rxjs';
import { AnalyticsService } from '../../services/analytics.service';
import { WebSocketService } from '../../services/websocket.service';
import { PredictiveAnalytics } from '../../models/predictive-analytics.model';

@Component({
  selector: 'app-predictive-analytics',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './predictive-analytics.component.html',
  styleUrl: './predictive-analytics.component.scss'
})
export class PredictiveAnalyticsComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  predictiveData: PredictiveAnalytics | null = null;
  selectedRegion = 'california';
  selectedForecastPeriod = '24h';
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
  
  forecastPeriods = [
    { value: '24h', label: '24 Hours' },
    { value: '48h', label: '48 Hours' },
    { value: '72h', label: '72 Hours' }
  ];
  
  // Expose Math to template
  Math = Math;

  constructor(
    private analyticsService: AnalyticsService,
    private webSocketService: WebSocketService
  ) {}

  ngOnInit(): void {
    this.loadPredictiveData();
    this.setupAutoRefresh();
    this.setupRealTimeUpdates();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupAutoRefresh(): void {
    interval(120000) // 2 minutes
      .pipe(
        startWith(0),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        if (!this.isLoading) {
          this.loadPredictiveData(false);
        }
      });
  }

  private setupRealTimeUpdates(): void {
    this.webSocketService.getEnergyData()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          if (data && this.predictiveData) {
            // Update current forecast with real-time data
            const currentForecast = this.predictiveData.energyForecasts[0];
            if (currentForecast) {
              currentForecast.solarGeneration = data.solarGeneration;
              currentForecast.windGeneration = data.windGeneration;
              currentForecast.totalGeneration = data.totalGeneration;
              currentForecast.consumption = data.consumption;
            }
          }
        },
        error: (error) => {
          console.error('WebSocket energy data error:', error);
        }
      });
  }

  loadPredictiveData(showLoading: boolean = true): void {
    if (showLoading) {
      this.isLoading = true;
    }
    this.error = null;

    this.analyticsService.getPredictiveAnalytics(this.selectedRegion, this.selectedForecastPeriod)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.predictiveData = data;
          this.lastUpdated = new Date();
          this.isLoading = false;
        },
        error: (error) => {
          this.error = error.message;
          this.isLoading = false;
          console.error('Failed to load predictive data:', error);
        }
      });
  }

  onRegionChange(): void {
    this.loadPredictiveData();
  }

  onForecastPeriodChange(): void {
    this.loadPredictiveData();
  }

  refreshData(): void {
    this.loadPredictiveData();
  }

  getInsightIcon(type: string): string {
    switch (type) {
      case 'opportunity': return 'trending_up';
      case 'risk': return 'warning';
      case 'maintenance': return 'build';
      case 'efficiency': return 'speed';
      default: return 'info';
    }
  }

  getInsightColor(type: string): string {
    switch (type) {
      case 'opportunity': return 'text-green-600';
      case 'risk': return 'text-red-600';
      case 'maintenance': return 'text-orange-600';
      case 'efficiency': return 'text-blue-600';
      default: return 'text-gray-600';
    }
  }

  getImpactColor(impact: string): string {
    switch (impact) {
      case 'high': return 'bg-red-100 text-red-800';
      case 'medium': return 'bg-yellow-100 text-yellow-800';
      case 'low': return 'bg-green-100 text-green-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  }

  formatNumber(value: number, decimals: number = 1): string {
    return value.toFixed(decimals);
  }

  formatTime(timestamp: string): string {
    return new Date(timestamp).toLocaleTimeString('en-US', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
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

  getNext6HourForecasts() {
    if (!this.predictiveData?.energyForecasts) return [];
    return this.predictiveData.energyForecasts.slice(0, 6);
  }

  getHighPriorityInsights() {
    if (!this.predictiveData?.insights) return [];
    return this.predictiveData.insights.filter(insight => 
      insight.impact === 'high' || insight.actionRequired
    );
  }
}