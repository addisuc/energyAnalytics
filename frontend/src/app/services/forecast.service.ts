import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ForecastService {
  private readonly baseUrl = `${environment.apiUrl}/forecast`;

  constructor(private http: HttpClient) {}

  getForecast(type: string, region: string, days: number): Observable<any> {
    const endpoint = `${this.baseUrl}/${type}/${region}?days=${days}`;
    const headers = this.getAuthHeaders();
    return this.http.get<any>(endpoint, { headers });
  }

  getDemandForecast(region: string, days: number = 7): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.baseUrl}/demand/${region}?days=${days}`, { headers });
  }

  getGenerationForecast(region: string, days: number = 7): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.baseUrl}/generation/${region}?days=${days}`, { headers });
  }

  getPriceForecast(region: string, days: number = 7): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.baseUrl}/price/${region}?days=${days}`, { headers });
  }

  getComprehensiveForecast(region: string, days: number = 7): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.baseUrl}/comprehensive/${region}?days=${days}`, { headers });
  }

  private getAuthHeaders(): { [header: string]: string } {
    const token = localStorage.getItem('token');
    if (!token) {
      console.warn('No auth token available for forecast service');
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