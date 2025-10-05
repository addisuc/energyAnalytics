import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subject, takeUntil } from 'rxjs';
import { SubscriptionService } from '../../services/subscription.service';

@Component({
  selector: 'app-subscription',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './subscription.component.html',
  styleUrl: './subscription.component.scss'
})
export class SubscriptionComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  currentSubscription: any = null;
  availablePlans: { [key: string]: { price: number | string; apiLimit: number | string; regions: number | string } } | null = null;
  isLoading = false;
  error: string | null = null;

  constructor(private subscriptionService: SubscriptionService) {}

  ngOnInit(): void {
    console.log('SubscriptionComponent ngOnInit called');
    this.loadAvailablePlans();
    this.loadCurrentSubscription();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadCurrentSubscription(): void {
    // Get user data from localStorage as fallback
    const userData = localStorage.getItem('userData');
    if (userData) {
      const user = JSON.parse(userData);
      this.currentSubscription = {
        plan: user.subscriptionPlan || 'FREE',
        status: 'active',
        currentUsage: 0,
        usageLimit: user.subscriptionPlan === 'ENTERPRISE' ? -1 : 1000
      };
      console.log('Current subscription from localStorage:', this.currentSubscription);
    }
    
    // Also try to load from API
    this.subscriptionService.getCurrentSubscription()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          if (response.success) {
            this.currentSubscription = response.data;
            console.log('Current subscription loaded from API:', this.currentSubscription);
          }
        },
        error: (error) => {
          console.error('Failed to load current subscription from API:', error);
          // Keep the localStorage fallback
        }
      });
  }

  loadAvailablePlans(): void {
    console.log('Loading available plans...');
    this.isLoading = true;
    this.error = null;
    
    this.subscriptionService.getAvailablePlans()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          console.log('Plans API response:', response);
          this.isLoading = false;
          if (response && response.success) {
            this.availablePlans = response.data;
            console.log('Available plans set:', this.availablePlans);
          } else {
            console.error('Invalid plans response:', response);
            this.error = 'Invalid response format';
          }
        },
        error: (error) => {
          console.error('Plans API error:', error);
          this.isLoading = false;
          this.error = error.message || 'Failed to load plans';
        }
      });
  }

  getUsagePercentage(): number {
    if (!this.currentSubscription || this.currentSubscription.usageLimit === -1) return 0;
    return Math.min((this.currentSubscription.currentUsage / this.currentSubscription.usageLimit) * 100, 100);
  }

  isCurrentPlan(planName: unknown): boolean {
    return this.currentSubscription?.plan === String(planName);
  }

  formatPrice(price: unknown): string {
    return typeof price === 'number' ? `$${price}` : String(price);
  }

  getPlanValue(plan: any, key: string): any {
    return plan?.value?.[key];
  }

  getButtonText(planKey: unknown): string {
    const key = String(planKey);
    if (this.isCurrentPlan(key)) {
      return key === 'ENTERPRISE' ? 'Manage Plan' : 'Current Plan';
    }
    if (key === 'FREE') {
      return 'Free Plan';
    }
    return 'Upgrade';
  }

  getOrderedPlans(): any[] {
    if (!this.availablePlans) return [];
    
    const planOrder = ['FREE', 'BASIC', 'PRO', 'ENTERPRISE'];
    return planOrder.map(planKey => ({
      key: planKey,
      value: this.availablePlans![planKey]
    })).filter(plan => plan.value);
  }

  handlePlanAction(planKey: string): void {
    if (this.isCurrentPlan(planKey) && planKey === 'ENTERPRISE') {
      // Handle manage plan for Enterprise users
      console.log('Managing Enterprise plan...');
      // You can add navigation to plan management page or show modal
      alert('Plan management features coming soon!');
    } else if (!this.isCurrentPlan(planKey)) {
      // Handle upgrade to different plan
      console.log('Upgrading to plan:', planKey);
      alert(`Upgrading to ${planKey} plan - Payment integration coming soon!`);
    }
  }
}