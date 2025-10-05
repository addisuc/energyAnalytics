import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { AnalyticsService } from '../../services/analytics.service';

@Component({
  selector: 'app-historical-analytics',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './historical-analytics.component.html',
  styleUrl: './historical-analytics.component.scss'
})
export class HistoricalAnalyticsComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  historicalData: any = null;
  selectedRegion = 'north';
  selectedPeriod = 7;
  isLoading = false;
  error: string | null = null;

  regions = [
    { value: 'north', label: 'North Region' },
    { value: 'south', label: 'South Region' },
    { value: 'east', label: 'East Region' },
    { value: 'west', label: 'West Region' },
    { value: 'central', label: 'Central Region' }
  ];

  periods = [
    { value: 7, label: 'Last 7 Days' },
    { value: 30, label: 'Last 30 Days' },
    { value: 90, label: 'Last 90 Days' }
  ];

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.loadHistoricalData();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadHistoricalData(): void {
    this.isLoading = true;
    this.error = null;
    
    this.analyticsService.getHistoricalAnalytics(this.selectedRegion, this.selectedPeriod)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.historicalData = response.data;
          } else {
            this.error = response.error || 'Failed to load historical data';
          }
          this.isLoading = false;
        },
        error: (error) => {
          this.error = error.message || 'Failed to load historical data';
          this.isLoading = false;
        }
      });
  }

  onRegionChange(region: string): void {
    this.selectedRegion = region;
    this.loadHistoricalData();
  }

  onPeriodChange(period: number): void {
    this.selectedPeriod = period;
    this.loadHistoricalData();
  }

  getTrendIcon(trend: string): string {
    switch (trend) {
      case 'increasing': return 'trending_up';
      case 'decreasing': return 'trending_down';
      case 'stable': return 'trending_flat';
      default: return 'help_outline';
    }
  }

  getTrendColor(trend: string): string {
    switch (trend) {
      case 'increasing': return 'text-green-600';
      case 'decreasing': return 'text-red-600';
      case 'stable': return 'text-blue-600';
      default: return 'text-gray-600';
    }
  }
}