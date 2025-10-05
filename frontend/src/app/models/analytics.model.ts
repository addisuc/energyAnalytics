export interface EnergyGeneration {
  solar: number;
  wind: number;
  total: number;
}

export interface EnergyDashboard {
  currentGeneration: EnergyGeneration;
  consumption: number;
  efficiency: number;
  riskScore: number;
  timestamp: string;
  region: string;
}

export interface UserSubscription {
  plan: 'FREE' | 'BASIC' | 'PRO' | 'ENTERPRISE';
  status: 'ACTIVE' | 'EXPIRED' | 'CANCELLED';
  expiryDate: string;
  usageLimit: number;
  currentUsage: number;
}

export interface NavigationItem {
  id: string;
  label: string;
  icon: string;
  route: string;
  requiredPlan?: string[];
  children?: NavigationItem[];
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
  timestamp: string;
}