import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, take, switchMap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private readonly baseUrl = `${environment.apiUrl}/subscription`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  getCurrentSubscription(): Observable<any> {
    return this.authService.token$.pipe(
      filter(token => token !== null),
      take(1),
      switchMap(() => {
        const headers = this.getAuthHeaders();
        return this.http.get<any>(`${this.baseUrl}/current`, { headers });
      })
    );
  }

  getAvailablePlans(): Observable<any> {
    const headers = this.getAuthHeaders();
    return this.http.get<any>(`${this.baseUrl}/plans`, { headers });
  }

  private getAuthHeaders(): { [header: string]: string } {
    const token = localStorage.getItem('token');
    if (!token) {
      console.warn('No auth token available for subscription service');
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