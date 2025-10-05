import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { ReportService } from '../../services/report.service';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.scss'
})
export class ReportsComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  templates: any[] = [];
  generatedReport: any = null;
  isLoading = false;
  isGenerating = false;
  error: string | null = null;
  
  selectedRegion = 'north';
  selectedTemplate = 'comprehensive';
  selectedPeriod = 30;
  selectedFormat = 'PDF';
  
  showScheduleForm = false;
  scheduleData = {
    reportType: 'comprehensive',
    region: 'north',
    frequency: 'WEEKLY',
    format: 'PDF',
    recipients: ''
  };

  regions = [
    { value: 'north', label: 'North Region' },
    { value: 'south', label: 'South Region' },
    { value: 'east', label: 'East Region' },
    { value: 'west', label: 'West Region' },
    { value: 'central', label: 'Central Region' }
  ];

  periods = [
    { value: 7, label: '7 Days' },
    { value: 30, label: '30 Days' },
    { value: 90, label: '90 Days' }
  ];

  formats = [
    { value: 'PDF', label: 'PDF Report' },
    { value: 'Excel', label: 'Excel/CSV Export' }
  ];

  frequencies = [
    { value: 'DAILY', label: 'Daily' },
    { value: 'WEEKLY', label: 'Weekly' },
    { value: 'MONTHLY', label: 'Monthly' }
  ];

  constructor(private reportService: ReportService) {}

  ngOnInit(): void {
    this.loadTemplates();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadTemplates(): void {
    this.isLoading = true;
    this.reportService.getReportTemplates()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.templates = response.data;
          }
          this.isLoading = false;
        },
        error: (error) => {
          this.error = error.message;
          this.isLoading = false;
        }
      });
  }

  generateReport(): void {
    this.isGenerating = true;
    this.error = null;
    
    this.reportService.generateReport(this.selectedRegion, this.selectedTemplate, this.selectedPeriod)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.generatedReport = response.data;
          } else {
            this.error = response.error || 'Failed to generate report';
          }
          this.isGenerating = false;
        },
        error: (error) => {
          this.error = error.message || 'Failed to generate report';
          this.isGenerating = false;
        }
      });
  }

  downloadReport(): void {
    if (this.selectedFormat === 'PDF') {
      this.reportService.downloadPdfReport(this.selectedRegion, this.selectedTemplate, this.selectedPeriod);
    } else {
      this.reportService.downloadExcelReport(this.selectedRegion, this.selectedTemplate, this.selectedPeriod);
    }
  }

  scheduleReport(): void {
    this.reportService.scheduleReport(this.scheduleData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (response.success) {
            alert('Report scheduled successfully!');
            this.resetScheduleForm();
          }
        },
        error: (error) => {
          this.error = error.message;
        }
      });
  }

  resetScheduleForm(): void {
    this.showScheduleForm = false;
    this.scheduleData = {
      reportType: 'comprehensive',
      region: 'north',
      frequency: 'WEEKLY',
      format: 'PDF',
      recipients: ''
    };
  }

  getTemplateById(id: string): any {
    return this.templates.find(t => t.id === id);
  }

  formatRecommendations(recommendations: string[]): string {
    if (!recommendations || recommendations.length === 0) return 'No recommendations available';
    return recommendations.join('; ');
  }

  getPerformanceColor(rating: string): string {
    switch (rating?.toUpperCase()) {
      case 'EXCELLENT': return 'text-green-600';
      case 'GOOD': return 'text-blue-600';
      case 'FAIR': return 'text-yellow-600';
      case 'NEEDS_IMPROVEMENT': return 'text-red-600';
      default: return 'text-gray-600';
    }
  }

  getRiskColor(risk: string): string {
    switch (risk?.toUpperCase()) {
      case 'LOW': return 'text-green-600';
      case 'MODERATE': return 'text-yellow-600';
      case 'HIGH': return 'text-red-600';
      default: return 'text-gray-600';
    }
  }
}