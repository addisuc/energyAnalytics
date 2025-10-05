import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private socket: WebSocket | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectInterval = 5000;

  private connectionStatus$ = new BehaviorSubject<boolean>(false);
  private energyData$ = new BehaviorSubject<any>(null);
  private weatherData$ = new BehaviorSubject<any>(null);

  constructor() {
    this.connect();
  }

  private connect(): void {
    try {
      this.socket = new WebSocket(`ws://localhost:8080/ws/energy-data`);
      
      this.socket.onopen = () => {
        console.log('WebSocket connected');
        this.connectionStatus$.next(true);
        this.reconnectAttempts = 0;
      };

      this.socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        this.handleMessage(data);
      };

      this.socket.onclose = () => {
        console.log('WebSocket disconnected');
        this.connectionStatus$.next(false);
        this.attemptReconnect();
      };

      this.socket.onerror = (error) => {
        console.error('WebSocket error:', error);
      };
    } catch (error) {
      console.error('Failed to connect WebSocket:', error);
      this.attemptReconnect();
    }
  }

  private handleMessage(data: any): void {
    switch (data.type) {
      case 'ENERGY_UPDATE':
        this.energyData$.next(data.payload);
        break;
      case 'WEATHER_UPDATE':
        this.weatherData$.next(data.payload);
        break;
    }
  }

  private attemptReconnect(): void {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      setTimeout(() => {
        console.log(`Reconnecting... Attempt ${this.reconnectAttempts}`);
        this.connect();
      }, this.reconnectInterval);
    }
  }

  public getConnectionStatus(): Observable<boolean> {
    return this.connectionStatus$.asObservable();
  }

  public getEnergyData(): Observable<any> {
    return this.energyData$.asObservable();
  }

  public getWeatherData(): Observable<any> {
    return this.weatherData$.asObservable();
  }

  public disconnect(): void {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  }
}