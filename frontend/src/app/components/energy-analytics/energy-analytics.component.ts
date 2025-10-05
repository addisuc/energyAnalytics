import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil, interval, startWith } from 'rxjs';
import { AnalyticsService } from '../../services/analytics.service';
import { WebSocketService } from '../../services/websocket.service';
import { EnergyDashboard } from '../../models/analytics.model';

@Component({
  selector: 'app-energy-analytics',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './energy-analytics.component.html',
  styleUrl: './energy-analytics.component.scss'
})
export class EnergyAnalyticsComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  // Component State
  energyData: EnergyDashboard | null = null;
  selectedRegion = 'north';
  isLoading = false;
  error: string | null = null;
  lastUpdated: Date | null = null;
  
  // Expose Math to template
  Math = Math;
  
  // Available regions for selection
  availableRegions = [
    { value: 'north', label: 'North Region', code: 'N' },
    { value: 'south', label: 'South Region', code: 'S' },
    { value: 'east', label: 'East Region', code: 'E' },
    { value: 'west', label: 'West Region', code: 'W' },
    { value: 'central', label: 'Central Region', code: 'C' }
  ];

  constructor(
    private analyticsService: AnalyticsService,
    private webSocketService: WebSocketService
  ) {}

  ngOnInit(): void {
    this.initializeComponent();
    this.setupAutoRefresh();
    this.setupRealTimeUpdates();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Initialize component with initial data load
   */
  private initializeComponent(): void {
    this.loadEnergyData();
  }

  /**
   * Setup automatic data refresh every 30 seconds
   */
  private setupAutoRefresh(): void {
    interval(30000) // 30 seconds
      .pipe(
        startWith(0),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        if (!this.isLoading) {
          this.loadEnergyData(false); // Silent refresh
        }
      });
  }

  /**
   * Setup real-time WebSocket updates
   */
  private setupRealTimeUpdates(): void {
    this.webSocketService.getEnergyData()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          if (data && this.energyData) {
            this.energyData = {
              ...this.energyData,
              currentGeneration: {
                solar: data.solarGeneration,
                wind: data.windGeneration,
                total: data.totalGeneration
              },
              consumption: data.consumption,
              efficiency: data.efficiency
            };
            this.lastUpdated = new Date();
          }
        },
        error: (error) => {
          console.error('WebSocket energy data error:', error);
        }
      });
  }

  /**
   * Load energy data for selected region
   * @param showLoading - Whether to show loading indicator
   */
  loadEnergyData(showLoading: boolean = true): void {
    if (showLoading) {
      this.isLoading = true;
    }
    
    this.error = null;

    this.analyticsService.getEnergyDashboard(this.selectedRegion)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.energyData = {
            ...data,
            region: this.selectedRegion,
            timestamp: new Date().toISOString()
          };
          this.lastUpdated = new Date();
          this.isLoading = false;
        },
        error: (error) => {
          this.error = error.message || 'Failed to load energy data';
          this.isLoading = false;
          console.error('Energy data loading error:', error);
        }
      });
  }

  /**
   * Handle region selection change
   */
  onRegionChange(): void {
    this.loadEnergyData();
  }

  /**
   * Manual refresh of data
   */
  refreshData(): void {
    this.loadEnergyData();
  }

  /**
   * Get efficiency status color based on percentage
   */
  getEfficiencyColor(efficiency: number): string {
    if (efficiency >= 90) return 'text-green-600';
    if (efficiency >= 70) return 'text-yellow-600';
    return 'text-red-600';
  }

  /**
   * Get risk level based on risk score
   */
  getRiskLevel(riskScore: number): { level: string; color: string } {
    if (riskScore <= 20) {
      return { level: 'Low', color: 'text-green-600' };
    } else if (riskScore <= 50) {
      return { level: 'Medium', color: 'text-yellow-600' };
    } else if (riskScore <= 80) {
      return { level: 'High', color: 'text-orange-600' };
    } else {
      return { level: 'Critical', color: 'text-red-600' };
    }
  }

  /**
   * Get generation status based on supply vs demand
   */
  getGenerationStatus(): { status: string; color: string } {
    if (!this.energyData) {
      return { status: 'Unknown', color: 'text-gray-600' };
    }

    const { currentGeneration, consumption } = this.energyData;
    const surplus = currentGeneration.total - consumption;

    if (surplus > 50) {
      return { status: 'Surplus', color: 'text-green-600' };
    } else if (surplus > 0) {
      return { status: 'Balanced', color: 'text-blue-600' };
    } else if (surplus > -50) {
      return { status: 'Deficit', color: 'text-yellow-600' };
    } else {
      return { status: 'Critical Deficit', color: 'text-red-600' };
    }
  }

  /**
   * Format number with appropriate units
   */
  formatMegawatts(value: number): string {
    if (value >= 1000) {
      return `${(value / 1000).toFixed(1)} GW`;
    }
    return `${value.toFixed(1)} MW`;
  }

  /**
   * Get percentage for progress bars
   */
  getGenerationPercentage(type: 'solar' | 'wind'): number {
    if (!this.energyData) return 0;
    
    const total = this.energyData.currentGeneration.total;
    if (total === 0) return 0;
    
    const value = this.energyData.currentGeneration[type];
    return Math.min((value / total) * 100, 100);
  }

  /**
   * Get time since last update
   */
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