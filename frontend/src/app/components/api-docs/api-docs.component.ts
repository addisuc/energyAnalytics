import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { ApiDocsService } from '../../services/api-docs.service';

@Component({
  selector: 'app-api-docs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './api-docs.component.html',
  styleUrl: './api-docs.component.scss'
})
export class ApiDocsComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  apiInfo: any = null;
  sdks: any = null;
  apiStatus: any = null;
  codeExamples: any = null;
  
  selectedLanguage = 'javascript';
  isLoading = false;
  error: string | null = null;

  languages = [
    { value: 'javascript', label: 'JavaScript/Node.js' },
    { value: 'python', label: 'Python' },
    { value: 'curl', label: 'cURL' }
  ];

  constructor(private apiDocsService: ApiDocsService) {}

  ngOnInit(): void {
    this.loadApiInfo();
    this.loadSDKs();
    this.loadApiStatus();
    this.loadCodeExamples();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadApiInfo(): void {
    this.apiDocsService.getApiInfo()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.apiInfo = data;
        },
        error: (error) => {
          this.error = error.message;
        }
      });
  }

  loadSDKs(): void {
    this.apiDocsService.getAvailableSDKs()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.sdks = data;
        },
        error: (error) => {
          console.error('Failed to load SDKs:', error);
        }
      });
  }

  loadApiStatus(): void {
    this.apiDocsService.getApiStatus()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.apiStatus = data;
        },
        error: (error) => {
          console.error('Failed to load API status:', error);
        }
      });
  }

  loadCodeExamples(): void {
    this.apiDocsService.getCodeExamples(this.selectedLanguage)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.codeExamples = data;
        },
        error: (error) => {
          console.error('Failed to load code examples:', error);
        }
      });
  }

  onLanguageChange(): void {
    this.loadCodeExamples();
  }

  copyToClipboard(text: string): void {
    navigator.clipboard.writeText(text).then(() => {
      // Could add a toast notification here
      console.log('Copied to clipboard');
    });
  }

  getStatusColor(status: string): string {
    switch (status?.toLowerCase()) {
      case 'operational': return 'text-green-600';
      case 'degraded': return 'text-yellow-600';
      case 'down': return 'text-red-600';
      default: return 'text-gray-600';
    }
  }

  getRateLimitColor(plan: string): string {
    switch (plan) {
      case 'FREE': return 'bg-gray-100';
      case 'BASIC': return 'bg-blue-100';
      case 'PRO': return 'bg-purple-100';
      case 'ENTERPRISE': return 'bg-green-100';
      default: return 'bg-gray-100';
    }
  }

  openSwaggerUI(): void {
    window.open('/swagger-ui.html', '_blank');
  }

  getSdkIcon(sdkKey: string): string {
    switch (sdkKey) {
      case 'javascript': return 'code';
      case 'python': return 'smart_toy';
      case 'java': return 'coffee';
      case 'curl': return 'terminal';
      default: return 'code';
    }
  }
}