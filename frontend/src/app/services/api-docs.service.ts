import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiDocsService {
  private readonly baseUrl = `${environment.apiUrl}/docs`;

  constructor(private http: HttpClient) {}

  getApiInfo(): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.baseUrl}/info`, { headers });
  }

  getAvailableSDKs(): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.baseUrl}/sdks`, { headers });
  }

  getCodeExamples(language: string): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.baseUrl}/examples/${language}`, { headers });
  }

  getApiStatus(): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.baseUrl}/status`, { headers });
  }

  private getAuthHeaders(): { [header: string]: string } {
    const token = localStorage.getItem('token');
    if (!token) {
      console.warn('No auth token available for API docs service');
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