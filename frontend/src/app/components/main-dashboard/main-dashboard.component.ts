import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, Router } from '@angular/router';
import { NavigationComponent } from '../navigation/navigation.component';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-main-dashboard',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavigationComponent],
  template: `
    <div class="app-layout">
      <app-navigation></app-navigation>
      <main class="main-content">
        <header class="top-header">
          <div class="welcome-section">
            <span class="welcome-text">Welcome, {{ getUserDisplayName() }}!</span>
          </div>
          <div class="user-actions">
            <button class="signout-btn" (click)="signOut()">
              <i class="material-icons">logout</i>
              Sign Out
            </button>
          </div>
        </header>
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .app-layout {
      display: flex;
      min-height: 100vh;
      background: transparent;
    }

    .main-content {
      flex: 1;
      background: white;
      overflow-y: auto;
      margin-top: 60px;
    }

    .top-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem 1rem;
      background: white;
      border-bottom: 1px solid #e5e7eb;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
      flex-wrap: wrap;
      gap: 0.5rem;
      
      display: none;
    }
    
    @media (min-width: 768px) {
      .top-header {
        padding: 1rem 2rem;
        flex-wrap: nowrap;
      }
    }

    .welcome-text {
      font-size: 1rem;
      font-weight: 600;
      color: #374151;
    }
    
    @media (min-width: 768px) {
      .welcome-text {
        font-size: 1.125rem;
      }
    }

    .signout-btn {
      display: flex;
      align-items: center;
      gap: 0.25rem;
      padding: 0.5rem 0.75rem;
      background: #ef4444;
      color: white;
      border: none;
      border-radius: 0.375rem;
      font-weight: 500;
      cursor: pointer;
      transition: background-color 0.2s;
      font-size: 0.875rem;
      min-height: 44px;
    }
    
    @media (min-width: 768px) {
      .signout-btn {
        gap: 0.5rem;
        padding: 0.5rem 1rem;
        font-size: 1rem;
      }
    }

    .signout-btn:hover {
      background: #dc2626;
    }

    .signout-btn i {
      font-size: 1rem;
    }
  `]
})
export class MainDashboardComponent implements OnInit {
  currentUser: any = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.user$.subscribe(user => {
      this.currentUser = user;
    });
    
    // Get user data from localStorage if available
    const userData = localStorage.getItem('user');
    if (userData) {
      const parsedUser = JSON.parse(userData);
      this.currentUser = parsedUser;
    }
  }

  getUserDisplayName(): string {
    if (this.currentUser) {
      if (typeof this.currentUser === 'string') {
        return this.currentUser;
      }
      if (this.currentUser.firstName) {
        return this.currentUser.firstName;
      }
      if (this.currentUser.email) {
        return this.currentUser.email.split('@')[0];
      }
    }
    return 'User';
  }

  signOut(): void {
    this.authService.logout();
    setTimeout(() => {
      this.router.navigate(['/']);
    }, 100);
  }
}