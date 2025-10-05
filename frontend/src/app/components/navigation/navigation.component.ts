import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { NavigationItem, UserSubscription } from '../../models/analytics.model';
import { AuthService } from '../../services/auth.service';
import { WebSocketService } from '../../services/websocket.service';
import { SubscriptionService } from '../../services/subscription.service';

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.scss'
})
export class NavigationComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  currentUser: string | null = null;
  userSubscription: UserSubscription = {
    plan: 'FREE',
    status: 'ACTIVE',
    expiryDate: '',
    usageLimit: 100,
    currentUsage: 0
  };
  
  isMenuCollapsed = false;
  isWebSocketConnected = false;
  
  navigationItems: NavigationItem[] = [
    {
      id: 'energy-analytics',
      label: 'Energy Analytics',
      icon: 'bolt',
      route: '/dashboard/analytics'
    },
    {
      id: 'weather-correlation',
      label: 'Weather Correlation',
      icon: 'cloud',
      route: '/dashboard/weather-correlation'
    },
    {
      id: 'predictive-analytics',
      label: 'Predictive Analytics',
      icon: 'timeline',
      route: '/dashboard/predictive-analytics'
    },
    {
      id: 'historical-analytics',
      label: 'Historical Analytics',
      icon: 'history',
      route: '/dashboard/historical-analytics'
    },
    {
      id: 'forecasting',
      label: 'Energy Forecasting',
      icon: 'insights',
      route: '/dashboard/forecasting'
    },
    {
      id: 'alerts',
      label: 'Alert Management',
      icon: 'notifications',
      route: '/dashboard/alerts'
    },
    {
      id: 'reports',
      label: 'Reports & Export',
      icon: 'description',
      route: '/dashboard/reports'
    },
    {
      id: 'advanced-visualizations',
      label: 'Advanced Visualizations',
      icon: 'dashboard',
      route: '/dashboard/advanced-visualizations'
    },
    {
      id: 'api-docs',
      label: 'API Documentation',
      icon: 'integration_instructions',
      route: '/dashboard/api-docs'
    },
    {
      id: 'subscription',
      label: 'Subscription & Billing',
      icon: 'credit_card',
      route: '/dashboard/subscription'
    }
  ];

  constructor(
    private authService: AuthService,
    private router: Router,
    private webSocketService: WebSocketService,
    private subscriptionService: SubscriptionService
  ) {}

  ngOnInit(): void {
    this.initializeUserData();
    this.setupWebSocketStatus();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Initialize user data and subscription info
   */
  private initializeUserData(): void {
    this.authService.user$
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (user) => {
          this.currentUser = user;
          if (user) {
            this.loadUserSubscription();
          }
        },
        error: (error) => {
          console.error('Error loading user data:', error);
        }
      });
  }

  /**
   * Load user subscription details from backend
   */
  private loadUserSubscription(): void {
    this.subscriptionService.getCurrentSubscription()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.userSubscription = {
              plan: response.data.plan,
              status: response.data.status,
              expiryDate: response.data.startDate,
              usageLimit: response.data.usageLimit,
              currentUsage: response.data.currentUsage
            };
          }
        },
        error: (error) => {
          console.error('Failed to load subscription:', error);
        }
      });
  }

  /**
   * Check if user has access to a navigation item
   */
  hasAccess(item: NavigationItem): boolean {
    if (!item.requiredPlan || item.requiredPlan.length === 0) {
      return true;
    }
    
    return item.requiredPlan.includes(this.userSubscription.plan);
  }

  /**
   * Toggle mobile menu
   */
  toggleMenu(): void {
    this.isMenuCollapsed = !this.isMenuCollapsed;
  }

  /**
   * Navigate to route and close mobile menu
   */
  navigateTo(route: string): void {
    this.router.navigate([route]);
    this.isMenuCollapsed = false;
  }

  /**
   * Handle user logout
   */
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  /**
   * Get subscription status color
   */
  getSubscriptionStatusColor(): string {
    switch (this.userSubscription.status) {
      case 'ACTIVE':
        return 'text-green-600';
      case 'EXPIRED':
        return 'text-red-600';
      case 'CANCELLED':
        return 'text-gray-600';
      default:
        return 'text-gray-600';
    }
  }

  /**
   * Get usage percentage for progress bar
   */
  getUsagePercentage(): number {
    if (this.userSubscription.usageLimit === 0) return 0;
    return Math.min(
      (this.userSubscription.currentUsage / this.userSubscription.usageLimit) * 100,
      100
    );
  }

  /**
   * Check if usage is near limit
   */
  isUsageNearLimit(): boolean {
    return this.getUsagePercentage() > 80;
  }

  /**
   * Setup WebSocket connection status monitoring
   */
  private setupWebSocketStatus(): void {
    this.webSocketService.getConnectionStatus()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (connected) => {
          this.isWebSocketConnected = connected;
        },
        error: (error) => {
          console.error('WebSocket status error:', error);
          this.isWebSocketConnected = false;
        }
      });
  }
}