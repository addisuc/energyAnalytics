import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  private readonly baseUrl = `${environment.apiUrl}/alerts`;

  constructor(private http: HttpClient) {}

  createAlert(alertData: any): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.post<any>(this.baseUrl, alertData, { headers });
  }

  getUserAlerts(): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(this.baseUrl, { headers });
  }

  updateAlert(alertId: number, alertData: any): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.put<any>(`${this.baseUrl}/${alertId}`, alertData, { headers });
  }

  deleteAlert(alertId: number): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.delete<any>(`${this.baseUrl}/${alertId}`, { headers });
  }

  getAlertHistory(alertId: number): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.baseUrl}/${alertId}/history`, { headers });
  }

  getAlertStatistics(): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.baseUrl}/statistics`, { headers });
  }

  private getAuthHeaders(): { [header: string]: string } {
    const token = localStorage.getItem('token');
    if (!token) {
      console.warn('No auth token available for alert service');
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