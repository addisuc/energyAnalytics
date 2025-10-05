import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { AlertService } from '../../services/alert.service';

@Component({
  selector: 'app-alerts',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './alerts.component.html',
  styleUrl: './alerts.component.scss'
})
export class AlertsComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  alerts: any[] = [];
  statistics: any = null;
  isLoading = false;
  error: string | null = null;
  
  showCreateForm = false;
  editingAlert: any = null;
  
  newAlert = {
    name: '',
    region: 'north',
    alertType: 'DEMAND',
    thresholdValue: 0,
    thresholdOperator: 'GREATER_THAN',
    notificationMethod: 'EMAIL',
    webhookUrl: ''
  };

  regions = [
    { value: 'north', label: 'North Region' },
    { value: 'south', label: 'South Region' },
    { value: 'east', label: 'East Region' },
    { value: 'west', label: 'West Region' },
    { value: 'central', label: 'Central Region' }
  ];

  alertTypes = [
    { value: 'DEMAND', label: 'Energy Demand' },
    { value: 'GENERATION', label: 'Energy Generation' },
    { value: 'EFFICIENCY', label: 'Energy Efficiency' },
    { value: 'PRICE', label: 'Energy Price' }
  ];

  operators = [
    { value: 'GREATER_THAN', label: 'Greater Than' },
    { value: 'LESS_THAN', label: 'Less Than' },
    { value: 'EQUALS', label: 'Equals' }
  ];

  notificationMethods = [
    { value: 'EMAIL', label: 'Email' },
    { value: 'SMS', label: 'SMS' },
    { value: 'WEBHOOK', label: 'Webhook' }
  ];

  constructor(private alertService: AlertService) {}

  ngOnInit(): void {
    this.loadAlerts();
    this.loadStatistics();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadAlerts(): void {
    this.isLoading = true;
    this.alertService.getUserAlerts()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.alerts = response.data;
          }
          this.isLoading = false;
        },
        error: (error) => {
          this.error = error.message;
          this.isLoading = false;
        }
      });
  }

  loadStatistics(): void {
    this.alertService.getAlertStatistics()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.statistics = response.data;
          }
        },
        error: (error) => {
          console.error('Failed to load statistics:', error);
        }
      });
  }

  createAlert(): void {
    this.alertService.createAlert(this.newAlert)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.loadAlerts();
            this.loadStatistics();
            this.resetForm();
          }
        },
        error: (error) => {
          this.error = error.message;
        }
      });
  }

  editAlert(alert: any): void {
    this.editingAlert = { ...alert };
    this.showCreateForm = true;
  }

  updateAlert(): void {
    if (!this.editingAlert) return;
    
    this.alertService.updateAlert(this.editingAlert.id, this.editingAlert)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.loadAlerts();
            this.resetForm();
          }
        },
        error: (error) => {
          this.error = error.message;
        }
      });
  }

  deleteAlert(alertId: number): void {
    if (confirm('Are you sure you want to delete this alert?')) {
      this.alertService.deleteAlert(alertId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response) => {
            if (response.success) {
              this.loadAlerts();
              this.loadStatistics();
            }
          },
          error: (error) => {
            this.error = error.message;
          }
        });
    }
  }

  resetForm(): void {
    this.showCreateForm = false;
    this.editingAlert = null;
    this.newAlert = {
      name: '',
      region: 'north',
      alertType: 'DEMAND',
      thresholdValue: 0,
      thresholdOperator: 'GREATER_THAN',
      notificationMethod: 'EMAIL',
      webhookUrl: ''
    };
  }

  getAlertTypeLabel(type: string): string {
    const alertType = this.alertTypes.find(t => t.value === type);
    return alertType ? alertType.label : type;
  }

  getOperatorLabel(operator: string): string {
    const op = this.operators.find(o => o.value === operator);
    return op ? op.label : operator;
  }

  getStatusColor(isActive: boolean): string {
    return isActive ? 'text-green-600' : 'text-gray-600';
  }

  formatThreshold(alert: any): string {
    const unit = this.getUnitForAlertType(alert.alertType);
    return `${alert.thresholdValue} ${unit}`;
  }

  private getUnitForAlertType(type: string): string {
    switch (type) {
      case 'DEMAND':
      case 'GENERATION':
        return 'MW';
      case 'EFFICIENCY':
        return '%';
      case 'PRICE':
        return '$/MWh';
      default:
        return '';
    }
  }
}