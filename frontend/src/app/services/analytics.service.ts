import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, map, retry, filter, take, switchMap } from 'rxjs/operators';
import { EnergyDashboard, ApiResponse } from '../models/analytics.model';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private readonly baseUrl = `${environment.apiUrl}/analytics`;
  private energyDataSubject = new BehaviorSubject<EnergyDashboard | null>(null);
  
  public energyData$ = this.energyDataSubject.asObservable();

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  /**
   * Get energy dashboard data for a specific region
   * @param region - The region to get data for
   * @returns Observable<EnergyDashboard>
   */
  getEnergyDashboard(region: string): Observable<EnergyDashboard> {
    if (!region || region.trim().length === 0) {
      return throwError(() => new Error('Region parameter is required'));
    }

    console.log('Fetching energy data for region:', region);
    
    return this.http.get<any>(
      `${this.baseUrl}/energy/${encodeURIComponent(region)}`
    ).pipe(
      retry(1),
      map((response: any) => {
        if (!response.success || !response.data) {
          throw new Error(response.error || 'Invalid response format');
        }
        
        this.energyDataSubject.next(response.data);
        return response.data;
      }),
      catchError((error) => {
        console.warn('API unavailable, using fallback data:', error);
        const fallbackData = this.getFallbackData(region);
        this.energyDataSubject.next(fallbackData);
        return new Observable<EnergyDashboard>(observer => {
          observer.next(fallbackData);
          observer.complete();
        });
      })
    );
  }

  /**
   * Get energy trends for a region over specified days
   * @param region - The region to analyze
   * @param days - Number of days to analyze (default: 7)
   * @returns Observable<any[]>
   */
  getEnergyTrends(region: string, days: number = 7): Observable<any[]> {
    if (!region || region.trim().length === 0) {
      return throwError(() => new Error('Region parameter is required'));
    }

    if (days < 1 || days > 365) {
      return throwError(() => new Error('Days must be between 1 and 365'));
    }

    return this.http.get<any>(
      `${this.baseUrl}/trends/${encodeURIComponent(region)}?days=${days}`
    ).pipe(
      retry(2),
      map((response: any) => {
        if (!response.success) {
          throw new Error(response.error || 'Failed to fetch trends data');
        }
        return response.data || [];
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Get weather correlation data for a region
   * @param region - The region to analyze
   * @param timeRange - Time range for analysis (24h, 7d, 30d)
   * @returns Observable<any>
   */
  getWeatherCorrelation(region: string, timeRange: string = '24h'): Observable<any> {
    if (!region || region.trim().length === 0) {
      return throwError(() => new Error('Region parameter is required'));
    }

    return this.http.get<any>(
      `${this.baseUrl}/weather-correlation/${encodeURIComponent(region)}?timeRange=${timeRange}`
    ).pipe(
      retry(2),
      map((response: any) => {
        if (!response.success || !response.data) {
          throw new Error(response.error || 'Invalid response format');
        }
        return response.data;
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Get predictive analytics data for a region
   * @param region - The region to analyze
   * @param forecastPeriod - Forecast period (24h, 48h, 72h)
   * @returns Observable<any>
   */
  getPredictiveAnalytics(region: string, forecastPeriod: string = '24h'): Observable<any> {
    if (!region || region.trim().length === 0) {
      return throwError(() => new Error('Region parameter is required'));
    }

    return this.http.get<any>(
      `${this.baseUrl}/predictive/${encodeURIComponent(region)}?forecastPeriod=${forecastPeriod}`
    ).pipe(
      retry(2),
      map((response: any) => {
        if (!response.success || !response.data) {
          throw new Error(response.error || 'Invalid response format');
        }
        return response.data;
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Get historical analytics data for a region
   * @param region - The region to analyze
   * @param days - Number of days to analyze
   * @returns Observable<any>
   */
  getHistoricalAnalytics(region: string, days: number = 7): Observable<any> {
    if (!region || region.trim().length === 0) {
      return throwError(() => new Error('Region parameter is required'));
    }

    return this.http.get<any>(
      `${this.baseUrl}/historical/${encodeURIComponent(region)}?days=${days}`
    ).pipe(
      retry(2),
      map((response: any) => {
        if (!response.success || !response.data) {
          throw new Error(response.error || 'Invalid response format');
        }
        return response;
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Clear cached energy data
   */
  clearCache(): void {
    this.energyDataSubject.next(null);
  }



  /**
   * Get fallback data when API is unavailable
   * @private
   */
  private getFallbackData(region: string): EnergyDashboard {
    return {
      region,
      timestamp: new Date().toISOString(),
      currentGeneration: {
        solar: 75 + Math.random() * 50,
        wind: 100 + Math.random() * 75,
        total: 175 + Math.random() * 125
      },
      consumption: 200 + Math.random() * 100,
      efficiency: 65 + Math.random() * 25,
      riskScore: 15 + Math.random() * 30
    };
  }

  /**
   * Handle HTTP errors with proper error messages and fallback
   * @private
   */
  private handleError = (error: HttpErrorResponse): Observable<never> => {
    let errorMessage = 'Using demo data - backend unavailable';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Network error: ${error.error.message}`;
    } else {
      // Server-side error
      switch (error.status) {
        case 0:
          errorMessage = 'Cannot connect to server. Using demo data.';
          break;
        case 401:
          errorMessage = 'Authentication failed. Please log in again.';
          break;
        case 403:
          errorMessage = 'Access denied. Check your subscription plan.';
          break;
        case 404:
          errorMessage = 'Requested data not found.';
          break;
        case 429:
          errorMessage = 'Rate limit exceeded. Please try again later.';
          break;
        case 500:
          errorMessage = 'Server error. Using demo data.';
          break;
        default:
          errorMessage = `Error ${error.status}: Using demo data`;
      }
    }
    
    console.warn('Analytics Service Error:', error);
    console.log('Falling back to demo data');
    return throwError(() => new Error(errorMessage));
  };
}