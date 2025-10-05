import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="register-container">
      <div class="register-card">
        <h2>Create Your Account</h2>
        <p class="subtitle">Join Energy Analytics Platform</p>
        
        <form (ngSubmit)="onSubmit()" #registerForm="ngForm">
          <div class="form-row">
            <div class="form-group">
              <label for="firstName">First Name *</label>
              <input type="text" id="firstName" name="firstName" 
                     [(ngModel)]="user.firstName" required #firstName="ngModel">
              <div *ngIf="firstName.invalid && firstName.touched" class="error">
                First name is required
              </div>
            </div>
            
            <div class="form-group">
              <label for="lastName">Last Name *</label>
              <input type="text" id="lastName" name="lastName" 
                     [(ngModel)]="user.lastName" required #lastName="ngModel">
              <div *ngIf="lastName.invalid && lastName.touched" class="error">
                Last name is required
              </div>
            </div>
          </div>

          <div class="form-group">
            <label for="email">Email Address *</label>
            <input type="email" id="email" name="email" 
                   [(ngModel)]="user.email" required email #email="ngModel">
            <div *ngIf="email.invalid && email.touched" class="error">
              Please enter a valid email address
            </div>
          </div>

          <div class="form-group">
            <label for="password">Password *</label>
            <input type="password" id="password" name="password" 
                   [(ngModel)]="user.password" required minlength="8" #password="ngModel">
            <div *ngIf="password.invalid && password.touched" class="error">
              Password must be at least 8 characters long
            </div>
          </div>

          <div class="form-group">
            <label for="confirmPassword">Confirm Password *</label>
            <input type="password" id="confirmPassword" name="confirmPassword" 
                   [(ngModel)]="user.confirmPassword" required #confirmPassword="ngModel">
            <div *ngIf="confirmPassword.invalid && confirmPassword.touched" class="error">
              Please confirm your password
            </div>
            <div *ngIf="user.password && user.confirmPassword && user.password !== user.confirmPassword" class="error">
              Passwords do not match
            </div>
          </div>

          <div class="form-group">
            <label for="companyName">Company Name *</label>
            <input type="text" id="companyName" name="companyName" 
                   [(ngModel)]="user.companyName" required #companyName="ngModel">
            <div *ngIf="companyName.invalid && companyName.touched" class="error">
              Company name is required
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="jobTitle">Job Title</label>
              <input type="text" id="jobTitle" name="jobTitle" 
                     [(ngModel)]="user.jobTitle">
            </div>
            
            <div class="form-group">
              <label for="phoneNumber">Phone Number</label>
              <input type="tel" id="phoneNumber" name="phoneNumber" 
                     [(ngModel)]="user.phoneNumber">
            </div>
          </div>

          <div class="form-group">
            <label for="subscriptionPlan">Subscription Plan *</label>
            <select id="subscriptionPlan" name="subscriptionPlan" 
                    [(ngModel)]="user.subscriptionPlan" required #plan="ngModel">
              <option value="">Select a plan</option>
              <option value="FREE">Free - Basic features, 7-day data</option>
              <option value="BASIC">Basic - $29/month, 5 regions, 30-day data</option>
              <option value="PRO">Pro - $99/month, 20 regions, 1-year data, alerts</option>
              <option value="ENTERPRISE">Enterprise - $299/month, unlimited access</option>
            </select>
            <div *ngIf="plan.invalid && plan.touched" class="error">
              Please select a subscription plan
            </div>
          </div>

          <div *ngIf="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>

          <div *ngIf="successMessage" class="success-message">
            {{ successMessage }}
          </div>

          <button type="submit" [disabled]="registerForm.invalid || isLoading || (user.password !== user.confirmPassword)" 
                  class="register-btn">
            {{ isLoading ? 'Creating Account...' : 'Create Account' }}
          </button>
        </form>

        <div class="login-link">
          Already have an account? 
          <a routerLink="/login" class="link">Sign in here</a>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .register-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
    }

    .register-card {
      background: white;
      padding: 40px;
      border-radius: 12px;
      box-shadow: 0 20px 40px rgba(0,0,0,0.1);
      width: 100%;
      max-width: 600px;
    }

    h2 {
      text-align: center;
      margin-bottom: 8px;
      color: #333;
      font-size: 28px;
    }

    .subtitle {
      text-align: center;
      color: #666;
      margin-bottom: 30px;
    }

    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
    }

    .form-group {
      margin-bottom: 20px;
    }

    label {
      display: block;
      margin-bottom: 5px;
      font-weight: 500;
      color: #333;
    }

    input, select {
      width: 100%;
      padding: 12px;
      border: 2px solid #e1e5e9;
      border-radius: 6px;
      font-size: 16px;
      transition: border-color 0.3s;
    }

    input:focus, select:focus {
      outline: none;
      border-color: #667eea;
    }

    .error {
      color: #e74c3c;
      font-size: 14px;
      margin-top: 5px;
    }

    .error-message {
      background: #fee;
      color: #e74c3c;
      padding: 12px;
      border-radius: 6px;
      margin-bottom: 20px;
      text-align: center;
    }

    .success-message {
      background: #efe;
      color: #27ae60;
      padding: 12px;
      border-radius: 6px;
      margin-bottom: 20px;
      text-align: center;
    }

    .register-btn {
      width: 100%;
      padding: 14px;
      background: #667eea;
      color: white;
      border: none;
      border-radius: 6px;
      font-size: 16px;
      font-weight: 500;
      cursor: pointer;
      transition: background 0.3s;
    }

    .register-btn:hover:not(:disabled) {
      background: #5a6fd8;
    }

    .register-btn:disabled {
      background: #ccc;
      cursor: not-allowed;
    }

    .login-link {
      text-align: center;
      margin-top: 20px;
      color: #666;
    }

    .link {
      color: #667eea;
      cursor: pointer;
      text-decoration: none;
    }

    .link:hover {
      text-decoration: underline;
    }

    @media (max-width: 768px) {
      .form-row {
        grid-template-columns: 1fr;
      }
      
      .register-card {
        padding: 30px 20px;
      }
    }
  `]
})
export class RegisterComponent {
  user = {
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    companyName: '',
    jobTitle: '',
    phoneNumber: '',
    subscriptionPlan: ''
  };

  isLoading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private http: HttpClient, private router: Router) {}

  onSubmit() {
    if (this.user.password !== this.user.confirmPassword) {
      this.errorMessage = 'Passwords do not match';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    // Remove confirmPassword before sending to backend
    const { confirmPassword, ...userData } = this.user;
    this.http.post<any>('http://localhost:8080/api/auth/register', userData)
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          if (response.success) {
            this.successMessage = 'Account created successfully! Redirecting to login...';
            setTimeout(() => {
              this.goToLogin();
            }, 2000);
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
        }
      });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}