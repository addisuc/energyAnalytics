import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="login-container">
      <div class="login-card">
        <h1>EnergyFlow Analytics</h1>
        <form (ngSubmit)="onSubmit()" #loginForm="ngForm">
          <div class="form-group">
            <input 
              type="email" 
              [(ngModel)]="email" 
              name="email"
              placeholder="Email"
              required>
          </div>
          <div class="form-group">
            <input 
              type="password" 
              [(ngModel)]="password" 
              name="password"
              placeholder="Password"
              required>
          </div>
          <button type="submit" [disabled]="!loginForm.valid || isLoading">
            {{ isLoading ? 'Signing in...' : 'Sign In' }}
          </button>
          <div *ngIf="error" class="error">{{ error }}</div>
        </form>
        <p class="register-link">
          Don't have an account? 
          <a (click)="showRegister = !showRegister">Register here</a>
        </p>
        
        <!-- Quick Register Form -->
        <div *ngIf="showRegister" class="register-form">
          <h2>Register</h2>
          <form (ngSubmit)="onRegister()" #regForm="ngForm">
            <input type="text" [(ngModel)]="regData.firstName" name="firstName" placeholder="First Name" required>
            <input type="text" [(ngModel)]="regData.lastName" name="lastName" placeholder="Last Name" required>
            <input type="email" [(ngModel)]="regData.email" name="regEmail" placeholder="Email" required>
            <input type="password" [(ngModel)]="regData.password" name="regPassword" placeholder="Password" required>
            <button type="submit" [disabled]="!regForm.valid">Register</button>
          </form>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    .login-card {
      background: white;
      padding: 2rem;
      border-radius: 8px;
      box-shadow: 0 4px 6px rgba(0,0,0,0.1);
      width: 100%;
      max-width: 400px;
    }
    .form-group {
      margin-bottom: 1rem;
    }
    input {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      box-sizing: border-box;
    }
    button {
      width: 100%;
      padding: 0.75rem;
      background: #667eea;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
    button:disabled {
      opacity: 0.6;
    }
    .error {
      color: red;
      margin-top: 0.5rem;
    }
    .register-link {
      text-align: center;
      margin-top: 1rem;
    }
    .register-form {
      margin-top: 1rem;
      padding-top: 1rem;
      border-top: 1px solid #eee;
    }
  `]
})
export class LoginComponent {
  email = '';
  password = '';
  isLoading = false;
  error = '';
  showRegister = false;
  
  regData = {
    firstName: '',
    lastName: '',
    email: '',
    password: ''
  };

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    this.isLoading = true;
    this.error = '';
    
    this.authService.loginUser(this.email, this.password).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.error = error.message;
        this.isLoading = false;
      }
    });
  }

  onRegister() {
    this.authService.registerUser(
      this.regData.firstName,
      this.regData.email,
      this.regData.password,
      this.regData.firstName,
      this.regData.lastName
    ).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.error = error.message;
      }
    });
  }
}