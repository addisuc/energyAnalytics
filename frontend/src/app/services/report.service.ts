import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private readonly baseUrl = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  generateReport(region: string, reportType: string = 'comprehensive', days: number = 30): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.baseUrl}/generate/${region}?reportType=${reportType}&days=${days}`, { headers });
  }

  getReportTemplates(): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.baseUrl}/templates`, { headers });
  }

  scheduleReport(scheduleData: any): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.post<any>(`${this.baseUrl}/schedule`, scheduleData, { headers });
  }

  downloadPdfReport(region: string, reportType: string = 'comprehensive', days: number = 30): void {
    const token = localStorage.getItem('token');
    const url = `${this.baseUrl}/download/pdf/${region}?reportType=${reportType}&days=${days}&token=${token}`;
    this.downloadFile(url, `energy-report-${region}-${reportType}.pdf`);
  }

  downloadExcelReport(region: string, reportType: string = 'comprehensive', days: number = 30): void {
    const token = localStorage.getItem('token');
    const url = `${this.baseUrl}/download/excel/${region}?reportType=${reportType}&days=${days}&token=${token}`;
    this.downloadFile(url, `energy-report-${region}-${reportType}.csv`);
  }

  private downloadFile(url: string, filename: string): void {
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.target = '_blank';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  private getAuthHeaders(): { [header: string]: string } {
    const token = localStorage.getItem('token');
    if (!token) {
      console.warn('No auth token available for report service');
      return {
        'Content-Type': 'application/json'
      };
    }
    
    return {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    };
  }
}