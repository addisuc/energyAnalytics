import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil, interval } from 'rxjs';
import { AnalyticsService } from '../../services/analytics.service';
import { Chart, ChartConfiguration, ChartType, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-advanced-visualizations',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './advanced-visualizations.component.html',
  styleUrl: './advanced-visualizations.component.scss'
})
export class AdvancedVisualizationsComponent implements OnInit, OnDestroy, AfterViewInit {
  private destroy$ = new Subject<void>();
  
  @ViewChild('realtimeChart', { static: false }) realtimeChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('regionChart', { static: false }) regionChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('trendChart', { static: false }) trendChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('heatmapChart', { static: false }) heatmapChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('geomapChart', { static: false }) geomapChartRef!: ElementRef<HTMLCanvasElement>;
  
  private realtimeChart?: Chart;
  private regionChart?: Chart;
  private trendChart?: Chart;
  private heatmapChart?: Chart;
  private geomapChart?: Chart;
  
  selectedRegion = 'north';
  selectedVisualization = 'realtime';
  isStreaming = false;
  
  realtimeData: number[] = [];
  realtimeLabels: string[] = [];
  
  regions = [
    { value: 'north', label: 'North Region' },
    { value: 'south', label: 'South Region' },
    { value: 'east', label: 'East Region' },
    { value: 'west', label: 'West Region' },
    { value: 'central', label: 'Central Region' }
  ];

  visualizations = [
    { value: 'realtime', label: 'Real-time Streaming' },
    { value: 'regional', label: 'Regional Comparison' },
    { value: 'trends', label: 'Historical Trends' },
    { value: 'heatmap', label: 'Performance Heatmap' },
    { value: 'geomap', label: 'Geospatial Analysis Map' }
  ];

  constructor(private analyticsService: AnalyticsService) {}

  ngOnInit(): void {
    this.initializeRealtimeData();
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.initializeCharts();
    }, 100);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.destroyCharts();
  }

  initializeRealtimeData(): void {
    // Initialize with 20 data points
    const now = new Date();
    for (let i = 19; i >= 0; i--) {
      const time = new Date(now.getTime() - i * 30000); // 30 seconds intervals
      this.realtimeLabels.push(time.toLocaleTimeString());
      this.realtimeData.push(Math.random() * 100 + 50); // Random efficiency data
    }
  }

  initializeCharts(): void {
    // Only initialize the chart for the currently selected visualization
    switch (this.selectedVisualization) {
      case 'realtime':
        this.createRealtimeChart();
        break;
      case 'regional':
        this.createRegionalChart();
        break;
      case 'trends':
        this.createTrendChart();
        break;
      case 'heatmap':
        this.createHeatmapChart();
        break;
      case 'geomap':
        this.createGeomapChart();
        break;
    }
  }

  createRealtimeChart(): void {
    if (!this.realtimeChartRef?.nativeElement) return;

    const config: ChartConfiguration = {
      type: 'line',
      data: {
        labels: this.realtimeLabels,
        datasets: [{
          label: 'Energy Efficiency (%)',
          data: this.realtimeData,
          borderColor: '#3b82f6',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          tension: 0.4,
          fill: true
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        layout: {
          padding: {
            top: 20,
            bottom: 10,
            left: 10,
            right: 10
          }
        },
        animation: {
          duration: 750
        },
        scales: {
          y: {
            beginAtZero: true,
            max: 150,
            title: {
              display: true,
              text: 'Efficiency (%)'
            }
          },
          x: {
            title: {
              display: true,
              text: 'Time'
            }
          }
        },
        plugins: {
          legend: {
            display: true,
            position: 'top'
          },
          title: {
            display: true,
            text: 'Real-time Energy Efficiency'
          }
        }
      }
    };

    this.realtimeChart = new Chart(this.realtimeChartRef.nativeElement, config);
  }

  createRegionalChart(): void {
    if (!this.regionChartRef?.nativeElement) return;

    const config: ChartConfiguration = {
      type: 'radar',
      data: {
        labels: ['Generation', 'Consumption', 'Efficiency', 'Reliability', 'Cost', 'Sustainability'],
        datasets: [
          {
            label: 'North',
            data: [85, 78, 92, 88, 75, 90],
            borderColor: '#ef4444',
            backgroundColor: 'rgba(239, 68, 68, 0.2)'
          },
          {
            label: 'South',
            data: [78, 85, 88, 82, 80, 85],
            borderColor: '#10b981',
            backgroundColor: 'rgba(16, 185, 129, 0.2)'
          },
          {
            label: 'East',
            data: [90, 75, 85, 90, 70, 88],
            borderColor: '#f59e0b',
            backgroundColor: 'rgba(245, 158, 11, 0.2)'
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          r: {
            beginAtZero: true,
            max: 100
          }
        },
        plugins: {
          title: {
            display: true,
            text: 'Regional Performance Comparison'
          }
        }
      }
    };

    this.regionChart = new Chart(this.regionChartRef.nativeElement, config);
  }

  createTrendChart(): void {
    if (!this.trendChartRef?.nativeElement) return;

    const config: ChartConfiguration = {
      type: 'line',
      data: {
        labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
        datasets: [
          {
            label: 'Solar Generation',
            data: [45, 52, 68, 78, 85, 92, 95, 88, 75, 62, 48, 42],
            borderColor: '#f59e0b',
            backgroundColor: 'rgba(245, 158, 11, 0.1)',
            tension: 0.4
          },
          {
            label: 'Wind Generation',
            data: [65, 68, 62, 58, 55, 48, 45, 52, 68, 75, 78, 72],
            borderColor: '#06b6d4',
            backgroundColor: 'rgba(6, 182, 212, 0.1)',
            tension: 0.4
          },
          {
            label: 'Total Consumption',
            data: [120, 115, 110, 105, 100, 95, 98, 102, 108, 115, 125, 130],
            borderColor: '#ef4444',
            backgroundColor: 'rgba(239, 68, 68, 0.1)',
            tension: 0.4
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            title: {
              display: true,
              text: 'Energy (MW)'
            }
          }
        },
        plugins: {
          title: {
            display: true,
            text: 'Annual Energy Trends'
          }
        }
      }
    };

    this.trendChart = new Chart(this.trendChartRef.nativeElement, config);
  }

  createHeatmapChart(): void {
    if (!this.heatmapChartRef?.nativeElement) return;

    // Create a simple heatmap using bar chart
    const heatmapData = [];
    const labels = [];
    
    for (let hour = 0; hour < 24; hour++) {
      labels.push(`${hour}:00`);
      heatmapData.push(Math.random() * 100 + 20); // Random efficiency data
    }

    const config: ChartConfiguration = {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [{
          label: 'Hourly Efficiency',
          data: heatmapData,
          backgroundColor: heatmapData.map(value => {
            if (value > 80) return '#10b981'; // Green for high efficiency
            if (value > 60) return '#f59e0b'; // Yellow for medium efficiency
            return '#ef4444'; // Red for low efficiency
          }),
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            max: 120,
            title: {
              display: true,
              text: 'Efficiency (%)'
            }
          },
          x: {
            title: {
              display: true,
              text: 'Hour of Day'
            }
          }
        },
        plugins: {
          title: {
            display: true,
            text: '24-Hour Performance Heatmap'
          },
          legend: {
            display: false
          }
        }
      }
    };

    this.heatmapChart = new Chart(this.heatmapChartRef.nativeElement, config);
  }

  createGeomapChart(): void {
    if (!this.geomapChartRef?.nativeElement) return;

    const config: ChartConfiguration = {
      type: 'scatter',
      data: {
        datasets: [
          {
            label: 'Solar Plants',
            data: [
              { x: -74.0060, y: 40.7128 }, // NYC
              { x: -118.2437, y: 34.0522 }, // LA
              { x: -112.0740, y: 33.4484 }, // Phoenix
              { x: -80.1918, y: 25.7617 } // Miami
            ],
            backgroundColor: '#f59e0b',
            borderColor: '#d97706',
            pointRadius: 12
          },
          {
            label: 'Wind Plants',
            data: [
              { x: -87.6298, y: 41.8781 }, // Chicago
              { x: -104.9903, y: 39.7392 } // Denver
            ],
            backgroundColor: '#06b6d4',
            borderColor: '#0891b2',
            pointRadius: 10
          },
          {
            label: 'Other Plants',
            data: [
              { x: -95.3698, y: 29.7604 }, // Houston
              { x: -122.3321, y: 47.6062 } // Seattle
            ],
            backgroundColor: '#10b981',
            borderColor: '#059669',
            pointRadius: 8
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          x: {
            type: 'linear',
            position: 'bottom',
            title: {
              display: true,
              text: 'Longitude'
            },
            min: -130,
            max: -65
          },
          y: {
            title: {
              display: true,
              text: 'Latitude'
            },
            min: 20,
            max: 50
          }
        },
        plugins: {
          title: {
            display: true,
            text: 'US Energy Facilities - Geographic Distribution'
          }
        }
      }
    };

    this.geomapChart = new Chart(this.geomapChartRef.nativeElement, config);
  }

  startRealTimeStreaming(): void {
    if (this.isStreaming) return;
    
    this.isStreaming = true;
    
    interval(2000) // Update every 2 seconds
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (!this.isStreaming) return;
        
        // Add new data point
        const now = new Date();
        this.realtimeLabels.push(now.toLocaleTimeString());
        this.realtimeData.push(Math.random() * 100 + 50);
        
        // Keep only last 20 points
        if (this.realtimeLabels.length > 20) {
          this.realtimeLabels.shift();
          this.realtimeData.shift();
        }
        
        // Update chart
        if (this.realtimeChart) {
          this.realtimeChart.data.labels = this.realtimeLabels;
          this.realtimeChart.data.datasets[0].data = this.realtimeData;
          this.realtimeChart.update('none');
        }
      });
  }

  stopRealTimeStreaming(): void {
    this.isStreaming = false;
  }

  onVisualizationChange(): void {
    // Reinitialize charts when visualization changes
    setTimeout(() => {
      this.initializeCharts();
    }, 100);
  }

  onRegionChange(): void {
    // Could update charts based on selected region
    this.updateChartsForRegion();
  }

  private updateChartsForRegion(): void {
    // Update charts with region-specific data
    // This is a placeholder - in production, fetch real data
  }

  private destroyCharts(): void {
    this.realtimeChart?.destroy();
    this.regionChart?.destroy();
    this.trendChart?.destroy();
    this.heatmapChart?.destroy();
    this.geomapChart?.destroy();
  }

  exportChart(chartType: string): void {
    let chart: Chart | undefined;
    
    switch (chartType) {
      case 'realtime':
        chart = this.realtimeChart;
        break;
      case 'regional':
        chart = this.regionChart;
        break;
      case 'trends':
        chart = this.trendChart;
        break;
      case 'heatmap':
        chart = this.heatmapChart;
        break;
      case 'geomap':
        chart = this.geomapChart;
        break;
    }
    
    if (chart) {
      const url = chart.toBase64Image();
      const link = document.createElement('a');
      link.download = `${chartType}-chart.png`;
      link.href = url;
      link.click();
    }
  }
}