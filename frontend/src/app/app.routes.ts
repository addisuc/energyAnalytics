import { Routes } from '@angular/router';
import { RegisterComponent } from './components/register/register.component';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', component: RegisterComponent },
  { path: 'login', loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent) },
  { path: 'home', loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent) },
  { 
    path: 'dashboard', 
    loadComponent: () => import('./components/main-dashboard/main-dashboard.component').then(m => m.MainDashboardComponent),
    children: [
      { path: '', redirectTo: 'analytics', pathMatch: 'full' },
      { path: 'analytics', loadComponent: () => import('./components/energy-analytics/energy-analytics.component').then(m => m.EnergyAnalyticsComponent) },
      { path: 'weather-correlation', loadComponent: () => import('./components/weather-correlation/weather-correlation.component').then(m => m.WeatherCorrelationComponent) },
      { path: 'predictive-analytics', loadComponent: () => import('./components/predictive-analytics/predictive-analytics.component').then(m => m.PredictiveAnalyticsComponent) },
      { path: 'historical-analytics', loadComponent: () => import('./components/historical-analytics/historical-analytics.component').then(m => m.HistoricalAnalyticsComponent) },
      { path: 'forecasting', loadComponent: () => import('./components/forecasting/forecasting.component').then(m => m.ForecastingComponent) },
      { path: 'alerts', loadComponent: () => import('./components/alerts/alerts.component').then(m => m.AlertsComponent) },
      { path: 'reports', loadComponent: () => import('./components/reports/reports.component').then(m => m.ReportsComponent) },
      { path: 'advanced-visualizations', loadComponent: () => import('./components/advanced-visualizations/advanced-visualizations.component').then(m => m.AdvancedVisualizationsComponent) },
      { path: 'api-docs', loadComponent: () => import('./components/api-docs/api-docs.component').then(m => m.ApiDocsComponent) },
      { path: 'subscription', loadComponent: () => import('./components/subscription/subscription.component').then(m => m.SubscriptionComponent) }
    ]
  }
];