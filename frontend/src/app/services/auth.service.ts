import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthRequest, AuthResponse } from '../models/weather.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private tokenSubject = new BehaviorSubject<string | null>(null);
  private userSubject = new BehaviorSubject<string | null>(null);

  constructor(private http: HttpClient) {
    // Check for existing authentication
    const token = localStorage.getItem('token');
    const user = localStorage.getItem('user');
    
    if (token && user) {
      this.tokenSubject.next(token);
      const userData = JSON.parse(user);
      this.userSubject.next(userData.firstName || userData.email);
    }
  }

  get token$(): Observable<string | null> {
    return this.tokenSubject.asObservable();
  }

  get user$(): Observable<string | null> {
    return this.userSubject.asObservable();
  }

  get token(): string | null {
    return this.tokenSubject.value;
  }



  loginUser(email: string, password: string): Observable<AuthResponse> {
    return new Observable(observer => {
      this.login(email, password).subscribe({
        next: (response: any) => {
          console.log('Login response:', response);
          if (response?.success && response?.token) {
            console.log('Login successful, setting auth');
            this.setAuth(response.token, response.user);
            observer.next(response);
          } else {
            observer.error(new Error(response?.error || 'Login failed'));
          }
          observer.complete();
        },
        error: (error) => observer.error(error)
      });
    });
  }

  registerUser(username: string, email: string, password: string, firstName: string, lastName: string): Observable<any> {
    const request = { username, email, password, firstName, lastName };
    return new Observable(observer => {
      this.http.post<any>(`${environment.apiUrl}/auth/register`, request).subscribe({
        next: (response: any) => {
          if (response?.token) {
            this.setAuth(response.token, response.user);
          }
          observer.next(response);
          observer.complete();
        },
        error: (error) => observer.error(error)
      });
    });
  }

  login(email: string, password: string): Observable<any> {
    const request = { email, password };
    return this.http.post<any>(`${environment.apiUrl}/auth/login`, request);
  }

  register(username: string, password: string): Observable<any> {
    const request = { username, password };
    return this.http.post<any>(`${environment.apiUrl}/auth/register`, request);
  }

  private setAuth(token: string, user: any): void {
    console.log('Setting auth - Token:', token ? 'Present' : 'Missing');
    console.log('Setting auth - User:', user);
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    this.tokenSubject.next(token);
    this.userSubject.next(user.firstName || user.email);
    console.log('Auth set - Token stored:', localStorage.getItem('token') ? 'Yes' : 'No');
  }

  logout(): void {
    this.tokenSubject.next(null);
    this.userSubject.next(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('userData');
    localStorage.clear(); // Clear all localStorage for clean logout
  }
}