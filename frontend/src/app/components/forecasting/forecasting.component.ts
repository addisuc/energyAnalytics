import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { ForecastService } from '../../services/forecast.service';

@Component({
  selector: 'app-forecasting',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './forecasting.component.html',
  styleUrl: './forecasting.component.scss'
})
export class ForecastingComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  selectedRegion = 'north';
  selectedPeriod = 7;
  selectedForecastType = 'comprehensive';
  
  forecastData: any = null;
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
    { value: 3, label: '3 Days' },
    { value: 7, label: '7 Days' },
    { value: 14, label: '14 Days' }
  ];

  forecastTypes = [
    { value: 'comprehensive', label: 'Comprehensive Forecast' },
    { value: 'demand', label: 'Demand Forecast' },
    { value: 'generation', label: 'Generation Forecast' },
    { value: 'price', label: 'Price Forecast' }
  ];

  constructor(private forecastService: ForecastService) {}

  ngOnInit(): void {
    this.loadForecast();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadForecast(): void {
    this.isLoading = true;
    this.error = null;
    
    this.forecastService.getForecast(this.selectedForecastType, this.selectedRegion, this.selectedPeriod)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.forecastData = response.data;
          } else {
            this.error = response.error || 'Failed to load forecast';
          }
          this.isLoading = false;
        },
        error: (error) => {
          this.error = error.message || 'Failed to load forecast';
          this.isLoading = false;
        }
      });
  }

  onRegionChange(region: string): void {
    this.selectedRegion = region;
    this.loadForecast();
  }

  onPeriodChange(period: number): void {
    this.selectedPeriod = period;
    this.loadForecast();
  }

  onForecastTypeChange(type: string): void {
    this.selectedForecastType = type;
    this.loadForecast();
  }

  getRiskColor(risk: string): string {
    switch (risk?.toLowerCase()) {
      case 'low': return 'text-green-600';
      case 'moderate': return 'text-yellow-600';
      case 'high': return 'text-red-600';
      default: return 'text-gray-600';
    }
  }

  getTrendIcon(trend: string): string {
    switch (trend?.toLowerCase()) {
      case 'increasing': return 'trending_up';
      case 'decreasing': return 'trending_down';
      case 'stable': return 'trending_flat';
      default: return 'help_outline';
    }
  }

  formatPrice(price: number): string {
    return `$${price.toFixed(2)}/MWh`;
  }

  formatGeneration(generation: number): string {
    return `${generation.toFixed(1)} MW`;
  }

  formatDemand(demand: number): string {
    return `${demand.toFixed(1)} MW`;
  }

  getForecastRows(): any[] {
    if (!this.forecastData) return [];
    
    switch (this.selectedForecastType) {
      case 'demand':
        return this.forecastData.dailyForecasts || [];
      case 'generation':
        return this.forecastData.forecasts || [];
      case 'price':
        return this.forecastData.priceForecasts || [];
      case 'comprehensive':
        // Combine data from different forecast types
        const demandData = this.forecastData.demandForecast?.dailyForecasts || [];
        const generationData = this.forecastData.generationForecast?.forecasts || [];
        const priceData = this.forecastData.priceForecast?.priceForecasts || [];
        
        // Merge data by date
        const combined = demandData.map((demand: any, index: number) => ({
          date: demand.date,
          predictedDemand: demand.predictedDemand,
          totalGeneration: generationData[index]?.totalGeneration || 0,
          predictedPrice: priceData[index]?.predictedPrice || 0,
          confidence: (demand.confidence + (generationData[index]?.confidence || 0) + (priceData[index]?.confidence || 0)) / 3
        }));
        
        return combined;
      default:
        return [];
    }
  }
}