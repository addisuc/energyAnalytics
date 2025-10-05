import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <div class="home-container">
      <!-- Header -->
      <header class="header">
        <div class="nav-container">
          <div class="logo">
            <h1>Energy Analytics</h1>
            <span>Professional Platform</span>
          </div>
          <nav class="nav-buttons">
            <button routerLink="/login" class="btn btn-outline">Login</button>
            <button routerLink="/register" class="btn btn-primary">Get Started</button>
          </nav>
        </div>
      </header>

      <!-- Hero Section -->
      <section class="hero">
        <div class="hero-content">
          <h1>Transform Your Energy Operations with Advanced Analytics</h1>
          <p>Harness the power of weather data and predictive analytics to optimize energy production, reduce costs, and make data-driven decisions.</p>
          <div class="hero-buttons">
            <button routerLink="/register" class="btn btn-primary btn-large">Start Free Trial</button>
            <button class="btn btn-outline btn-large">Watch Demo</button>
          </div>
        </div>
        <div class="hero-visual">
          <div class="energy-animation">
            <div class="wind-turbine">
              <div class="turbine-tower"></div>
              <div class="turbine-blades">
                <div class="blade blade1"></div>
                <div class="blade blade2"></div>
                <div class="blade blade3"></div>
              </div>
            </div>
            <div class="solar-panel">
              <div class="panel-grid">
                <div class="panel-cell" *ngFor="let cell of cells"></div>
              </div>
              <div class="energy-flow">
                <div class="energy-particle" *ngFor="let p of particles"></div>
              </div>
            </div>
            <div class="power-grid">
              <div class="grid-line"></div>
              <div class="grid-node"></div>
              <div class="grid-pulse"></div>
            </div>
          </div>
        </div>
      </section>

      <!-- Features -->
      <section class="features">
        <div class="container">
          <h2>Comprehensive Energy Intelligence</h2>
          <div class="features-grid">
            <div class="feature">
              <div class="feature-icon">⚡</div>
              <h3>Real-time Monitoring</h3>
              <p>Track energy production and consumption with live weather correlation</p>
            </div>
            <div class="feature">
              <div class="feature-icon">📊</div>
              <h3>Predictive Analytics</h3>
              <p>Forecast energy demand and optimize resource allocation</p>
            </div>
            <div class="feature">
              <div class="feature-icon">🌤️</div>
              <h3>Weather Integration</h3>
              <p>Advanced weather data analysis for renewable energy planning</p>
            </div>
            <div class="feature">
              <div class="feature-icon">📈</div>
              <h3>Performance Insights</h3>
              <p>Detailed reports and analytics to maximize efficiency</p>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA Section -->
      <section class="cta">
        <div class="container">
          <h2>Ready to Optimize Your Energy Operations?</h2>
          <p>Join thousands of energy professionals using our platform</p>
          <button routerLink="/register" class="btn btn-primary btn-large">Start Your Free Trial</button>
        </div>
      </section>
    </div>
  `,
  styles: [`
    .home-container {
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .header {
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(10px);
      padding: 1rem 0;
    }

    .nav-container {
      max-width: 1200px;
      margin: 0 auto;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0 2rem;
    }

    .logo h1 {
      color: white;
      margin: 0;
      font-size: 1.5rem;
      font-weight: 700;
    }

    .logo span {
      color: rgba(255, 255, 255, 0.8);
      font-size: 0.875rem;
    }

    .nav-buttons {
      display: flex;
      gap: 1rem;
    }

    .btn {
      padding: 0.75rem 1.5rem;
      border-radius: 0.5rem;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s ease;
      text-decoration: none;
      display: inline-block;
      text-align: center;
      border: none;
    }

    .btn-primary {
      background: #3b82f6;
      color: white;
    }

    .btn-primary:hover {
      background: #2563eb;
    }

    .btn-outline {
      background: transparent;
      color: white;
      border: 1px solid rgba(255, 255, 255, 0.3);
    }

    .btn-outline:hover {
      background: rgba(255, 255, 255, 0.1);
    }

    .btn-large {
      padding: 1rem 2rem;
      font-size: 1.125rem;
    }

    .hero {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 4rem;
      max-width: 1200px;
      margin: 0 auto;
      padding: 4rem 2rem;
      align-items: center;
    }

    .hero-content h1 {
      font-size: 3rem;
      font-weight: 700;
      color: white;
      margin-bottom: 1.5rem;
      line-height: 1.2;
    }

    .hero-content p {
      font-size: 1.25rem;
      color: rgba(255, 255, 255, 0.9);
      margin-bottom: 2rem;
      line-height: 1.6;
    }

    .hero-buttons {
      display: flex;
      gap: 1rem;
    }

    .hero-visual {
      display: flex;
      justify-content: center;
    }

    .energy-animation {
      position: relative;
      width: 400px;
      height: 300px;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 1rem;
      backdrop-filter: blur(10px);
      overflow: hidden;
    }

    .wind-turbine {
      position: absolute;
      top: 50px;
      left: 50px;
    }

    .turbine-tower {
      width: 4px;
      height: 120px;
      background: #e5e7eb;
      margin: 0 auto;
    }

    .turbine-blades {
      position: relative;
      width: 60px;
      height: 60px;
      margin: -30px auto 0;
      animation: rotate 3s linear infinite;
    }

    .blade {
      position: absolute;
      width: 2px;
      height: 30px;
      background: white;
      top: 50%;
      left: 50%;
      transform-origin: bottom center;
    }

    .blade1 { transform: translate(-50%, -100%) rotate(0deg); }
    .blade2 { transform: translate(-50%, -100%) rotate(120deg); }
    .blade3 { transform: translate(-50%, -100%) rotate(240deg); }

    @keyframes rotate {
      from { transform: rotate(0deg); }
      to { transform: rotate(360deg); }
    }

    .solar-panel {
      position: absolute;
      top: 180px;
      right: 50px;
      width: 80px;
      height: 60px;
    }

    .panel-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 2px;
      width: 100%;
      height: 100%;
      background: #1f2937;
      padding: 4px;
      border-radius: 4px;
    }

    .panel-cell {
      background: #3b82f6;
      border-radius: 2px;
      animation: solar-pulse 2s ease-in-out infinite alternate;
    }

    @keyframes solar-pulse {
      from { opacity: 0.6; }
      to { opacity: 1; }
    }

    .energy-flow {
      position: absolute;
      top: 100%;
      left: 50%;
      width: 2px;
      height: 40px;
      background: #10b981;
    }

    .energy-particle {
      position: absolute;
      width: 4px;
      height: 4px;
      background: #10b981;
      border-radius: 50%;
      animation: flow 1.5s linear infinite;
    }

    .energy-particle:nth-child(2) { animation-delay: 0.3s; }
    .energy-particle:nth-child(3) { animation-delay: 0.6s; }
    .energy-particle:nth-child(4) { animation-delay: 0.9s; }
    .energy-particle:nth-child(5) { animation-delay: 1.2s; }

    @keyframes flow {
      from { top: 0; opacity: 1; }
      to { top: 40px; opacity: 0; }
    }

    .power-grid {
      position: absolute;
      bottom: 20px;
      left: 20px;
      right: 20px;
      height: 40px;
    }

    .grid-line {
      width: 100%;
      height: 2px;
      background: #6b7280;
      position: relative;
    }

    .grid-node {
      position: absolute;
      width: 8px;
      height: 8px;
      background: #f59e0b;
      border-radius: 50%;
      top: -3px;
      left: 50%;
      transform: translateX(-50%);
    }

    .grid-pulse {
      position: absolute;
      width: 20px;
      height: 20px;
      border: 2px solid #f59e0b;
      border-radius: 50%;
      top: -9px;
      left: 50%;
      transform: translateX(-50%);
      animation: pulse 2s ease-out infinite;
    }

    @keyframes pulse {
      from { transform: translateX(-50%) scale(0); opacity: 1; }
      to { transform: translateX(-50%) scale(1); opacity: 0; }
    }

    .features {
      background: white;
      padding: 4rem 0;
    }

    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 2rem;
      text-align: center;
    }

    .features h2 {
      font-size: 2.5rem;
      color: #1f2937;
      margin-bottom: 3rem;
    }

    .features-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 2rem;
    }

    .feature {
      padding: 2rem;
      border-radius: 1rem;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }

    .feature-icon {
      font-size: 3rem;
      margin-bottom: 1rem;
    }

    .feature h3 {
      font-size: 1.25rem;
      color: #1f2937;
      margin-bottom: 1rem;
    }

    .feature p {
      color: #6b7280;
      line-height: 1.6;
    }

    .cta {
      background: #1f2937;
      color: white;
      padding: 4rem 0;
      text-align: center;
    }

    .cta h2 {
      font-size: 2.5rem;
      margin-bottom: 1rem;
    }

    .cta p {
      font-size: 1.25rem;
      margin-bottom: 2rem;
      opacity: 0.9;
    }

    @media (max-width: 768px) {
      .nav-container {
        flex-direction: column;
        gap: 1rem;
      }

      .hero {
        grid-template-columns: 1fr;
        text-align: center;
      }

      .hero-content h1 {
        font-size: 2rem;
      }

      .hero-buttons {
        flex-direction: column;
      }

      .features-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class HomeComponent {
  // Animation data for ngFor
  cells = Array(9).fill(0).map((_, i) => i + 1);
  particles = Array(5).fill(0).map((_, i) => i + 1);
}