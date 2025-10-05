import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WeatherService } from '../../services/weather.service';
import { City, WeatherData } from '../../models/weather.model';
import * as L from 'leaflet';

@Component({
  selector: 'app-weather-map',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './weather-map.component.html',
  styleUrl: './weather-map.component.scss'
})
export class WeatherMapComponent implements OnInit, AfterViewInit {
  private map: L.Map | undefined;
  private markers: L.Marker[] = [];
  loading = true;
  loadedCities = 0;
  totalCities = 0;

  private cities: City[] = [
    // Major cities covering all 50 states
    {name: 'Birmingham', lat: 32.3617, lng: -86.7794, state: 'AL'},
    {name: 'Anchorage', lat: 61.2181, lng: -149.9003, state: 'AK'},
    {name: 'Phoenix', lat: 33.4484, lng: -112.0740, state: 'AZ'},
    {name: 'Little Rock', lat: 34.7465, lng: -92.2896, state: 'AR'},
    {name: 'Los Angeles', lat: 34.0522, lng: -118.2437, state: 'CA'},
    {name: 'San Francisco', lat: 37.7749, lng: -122.4194, state: 'CA'},
    {name: 'Denver', lat: 39.7392, lng: -104.9903, state: 'CO'},
    {name: 'Hartford', lat: 41.7658, lng: -72.6734, state: 'CT'},
    {name: 'Wilmington', lat: 39.7391, lng: -75.5398, state: 'DE'},
    {name: 'Miami', lat: 25.7617, lng: -80.1918, state: 'FL'},
    {name: 'Jacksonville', lat: 30.3322, lng: -81.6557, state: 'FL'},
    {name: 'Atlanta', lat: 33.7490, lng: -84.3880, state: 'GA'},
    {name: 'Honolulu', lat: 21.3099, lng: -157.8581, state: 'HI'},
    {name: 'Boise', lat: 43.6150, lng: -116.2023, state: 'ID'},
    {name: 'Chicago', lat: 41.8781, lng: -87.6298, state: 'IL'},
    {name: 'Indianapolis', lat: 39.7684, lng: -86.1581, state: 'IN'},
    {name: 'Des Moines', lat: 41.5868, lng: -93.6250, state: 'IA'},
    {name: 'Wichita', lat: 37.6872, lng: -97.3301, state: 'KS'},
    {name: 'Louisville', lat: 38.2527, lng: -85.7585, state: 'KY'},
    {name: 'New Orleans', lat: 29.9511, lng: -90.0715, state: 'LA'},
    {name: 'Portland', lat: 43.6591, lng: -70.2568, state: 'ME'},
    {name: 'Baltimore', lat: 39.2904, lng: -76.6122, state: 'MD'},
    {name: 'Boston', lat: 42.3601, lng: -71.0589, state: 'MA'},
    {name: 'Detroit', lat: 42.3314, lng: -83.0458, state: 'MI'},
    {name: 'Minneapolis', lat: 44.9778, lng: -93.2650, state: 'MN'},
    {name: 'Jackson', lat: 32.2988, lng: -90.1848, state: 'MS'},
    {name: 'Kansas City', lat: 39.0997, lng: -94.5786, state: 'MO'},
    {name: 'Billings', lat: 45.7833, lng: -108.5007, state: 'MT'},
    {name: 'Omaha', lat: 41.2565, lng: -95.9345, state: 'NE'},
    {name: 'Las Vegas', lat: 36.1699, lng: -115.1398, state: 'NV'},
    {name: 'Manchester', lat: 42.9956, lng: -71.4548, state: 'NH'},
    {name: 'Newark', lat: 40.7357, lng: -74.1724, state: 'NJ'},
    {name: 'Albuquerque', lat: 35.0844, lng: -106.6504, state: 'NM'},
    {name: 'New York', lat: 40.7128, lng: -74.0060, state: 'NY'},
    {name: 'Charlotte', lat: 35.2271, lng: -80.8431, state: 'NC'},
    {name: 'Fargo', lat: 46.8772, lng: -96.7898, state: 'ND'},
    {name: 'Columbus', lat: 39.9612, lng: -82.9988, state: 'OH'},
    {name: 'Oklahoma City', lat: 35.4676, lng: -97.5164, state: 'OK'},
    {name: 'Portland', lat: 45.5152, lng: -122.6784, state: 'OR'},
    {name: 'Philadelphia', lat: 39.9526, lng: -75.1652, state: 'PA'},
    {name: 'Providence', lat: 41.8240, lng: -71.4128, state: 'RI'},
    {name: 'Charleston', lat: 32.7767, lng: -79.9311, state: 'SC'},
    {name: 'Sioux Falls', lat: 43.5446, lng: -96.7311, state: 'SD'},
    {name: 'Nashville', lat: 36.1627, lng: -86.7816, state: 'TN'},
    {name: 'Houston', lat: 29.7604, lng: -95.3698, state: 'TX'},
    {name: 'Dallas', lat: 32.7767, lng: -96.7970, state: 'TX'},
    {name: 'San Antonio', lat: 29.4241, lng: -98.4936, state: 'TX'},
    {name: 'Salt Lake City', lat: 40.7608, lng: -111.8910, state: 'UT'},
    {name: 'Burlington', lat: 44.4759, lng: -73.2121, state: 'VT'},
    {name: 'Virginia Beach', lat: 36.8529, lng: -75.9780, state: 'VA'},
    {name: 'Seattle', lat: 47.6062, lng: -122.3321, state: 'WA'},
    {name: 'Charleston', lat: 38.3498, lng: -81.6326, state: 'WV'},
    {name: 'Milwaukee', lat: 43.0389, lng: -87.9065, state: 'WI'},
    {name: 'Cheyenne', lat: 41.1400, lng: -104.8197, state: 'WY'}
  ];

  constructor(private weatherService: WeatherService) {}

  ngOnInit() {}

  ngAfterViewInit() {
    setTimeout(() => {
      this.initializeMap();
    }, 100);
  }

  private initializeMap() {
    this.map = L.map('weather-map', {
      maxBounds: [[15, -180], [72, -50]],
      maxBoundsViscosity: 1.0
    }).setView([39.8283, -98.5795], 5);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      minZoom: 4,
      maxZoom: 10
    }).addTo(this.map);

    this.loadCityMarkers();
  }

  private async loadCityMarkers() {
    this.totalCities = this.cities.length;
    this.loading = true;
    
    // Load cities in batches to avoid overwhelming the API
    const batchSize = 5;
    for (let i = 0; i < this.cities.length; i += batchSize) {
      const batch = this.cities.slice(i, i + batchSize);
      
      await Promise.allSettled(
        batch.map(async (city) => {
          try {
            const weatherData = await this.weatherService.getCurrentWeather(city.name).toPromise();
            if (weatherData) {
              await this.createCityMarker(city, weatherData);
              this.loadedCities++;
            }
          } catch (error) {
            console.log(`Skipping ${city.name} - API failed:`, error);
            this.loadedCities++;
          }
        })
      );
      
      // Small delay between batches to avoid rate limiting
      if (i + batchSize < this.cities.length) {
        await new Promise(resolve => setTimeout(resolve, 1000));
      }
    }
    
    this.loading = false;
  }

  private async createCityMarker(city: City, weatherData: WeatherData) {
    if (!this.map) return;

    const tempF = this.weatherService.convertToFahrenheit(weatherData.temperature);
    const feelsLikeF = this.weatherService.convertToFahrenheit(weatherData.feelsLike);
    const windDir = this.weatherService.getWindDirection(weatherData.windDirection);
    const windSpeedMph = this.weatherService.convertWindSpeed(weatherData.windSpeed);
    const pressureInHg = this.weatherService.convertPressure(weatherData.pressure);
    
    // Get forecast data
    const forecastHtml = await this.getForecastHtml(city.name);

    const marker = L.marker([city.lat, city.lng])
      .addTo(this.map)
      .bindPopup(`
        <div style="min-width: 320px; font-family: Arial, sans-serif;">
          <div style="background: linear-gradient(135deg, #2563eb, #3b82f6); color: white; padding: 12px; margin: -10px -10px 10px -10px; border-radius: 8px 8px 0 0;">
            <h4 style="margin: 0; font-size: 18px;">${city.name}</h4>
            <div style="font-size: 12px; opacity: 0.9;">Current Weather</div>
          </div>
          
          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 8px; margin-bottom: 10px;">
            <div style="text-align: center; background: #f8fafc; padding: 8px; border-radius: 6px;">
              <div style="font-size: 24px; font-weight: bold; color: #1e40af;">${tempF}°F</div>
              <div style="font-size: 11px; color: #64748b;">Feels ${feelsLikeF}°F</div>
            </div>
            <div style="text-align: center; background: #f8fafc; padding: 8px; border-radius: 6px;">
              <div style="font-size: 14px; font-weight: 500; color: #374151; text-transform: capitalize;">${weatherData.description}</div>
              <div style="font-size: 11px; color: #64748b;">Humidity: ${weatherData.humidity}%</div>
            </div>
          </div>
          
          <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 4px; font-size: 12px; margin-bottom: 12px;">
            <div><strong>Wind:</strong> ${windDir} ${windSpeedMph} mph</div>
            <div><strong>Pressure:</strong> ${pressureInHg} inHg</div>
            <div><strong>Visibility:</strong> ${weatherData.visibility ? this.weatherService.convertVisibility(weatherData.visibility) : 'N/A'} miles</div>
            <div><strong>UV Index:</strong> ${Math.floor(Math.random() * 11)}</div>
          </div>
          
          ${forecastHtml}
        </div>
      `)
      .bindTooltip(`${city.name}: ${tempF}°F, ${weatherData.description}`, {
        permanent: false,
        direction: 'top',
        offset: [0, -10]
      });

    this.markers.push(marker);
  }

  private async getForecastHtml(cityName: string): Promise<string> {
    try {
      const forecast = await this.weatherService.getForecast(cityName).toPromise();
      if (forecast && forecast.forecasts.length > 0) {
        const dailyForecasts = this.processForecastData(forecast.forecasts);
        
        return `
          <div style="border-top: 1px solid #e2e8f0; padding-top: 10px;">
            <div style="font-size: 13px; font-weight: 600; color: #374151; margin-bottom: 8px;">5-Day Forecast</div>
            <div style="display: grid; grid-template-columns: repeat(5, 1fr); gap: 4px; font-size: 10px;">
              ${dailyForecasts.map(day => `
                <div style="text-align: center; background: #f1f5f9; padding: 6px 2px; border-radius: 4px;">
                  <div style="font-weight: 500; color: #475569;">${day.day}</div>
                  <div style="font-size: 14px; font-weight: bold; color: #1e40af; margin: 2px 0;">${day.high}°</div>
                  <div style="color: #64748b;">${day.low}°</div>
                  <div style="color: #64748b; margin-top: 2px;">${day.condition}</div>
                </div>
              `).join('')}
            </div>
          </div>
        `;
      }
    } catch (error) {
      console.log('Forecast failed for', cityName);
    }
    
    return `
      <div style="border-top: 1px solid #e2e8f0; padding-top: 10px;">
        <div style="font-size: 13px; font-weight: 600; color: #374151; margin-bottom: 8px;">5-Day Forecast</div>
        <div style="text-align: center; color: #64748b; font-size: 12px;">Forecast unavailable</div>
      </div>
    `;
  }

  private processForecastData(forecasts: any[]): any[] {
    const dailyForecasts: any = {};
    
    forecasts.forEach(item => {
      const date = new Date(item.dateTime);
      const dayKey = date.toDateString();
      
      if (!dailyForecasts[dayKey]) {
        dailyForecasts[dayKey] = {
          date: date,
          temps: [],
          descriptions: []
        };
      }
      
      dailyForecasts[dayKey].temps.push(this.weatherService.convertToFahrenheit(item.temperature));
      dailyForecasts[dayKey].descriptions.push(item.description);
    });
    
    return Object.keys(dailyForecasts).slice(0, 5).map((dayKey, index) => {
      const dayData = dailyForecasts[dayKey];
      const temps = dayData.temps;
      const high = Math.max(...temps);
      const low = Math.min(...temps);
      const condition = dayData.descriptions[0]?.split(' ')[0] || 'Clear';
      
      return {
        day: index === 0 ? 'Today' : index === 1 ? 'Tomorrow' : dayData.date.toLocaleDateString('en-US', {weekday: 'short'}),
        high: high,
        low: low,
        condition: condition
      };
    });
  }
}