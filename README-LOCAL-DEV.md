# Local Development Setup

## Architecture Overview

This is an Energy Analytics Platform with:
- **Backend**: Spring Boot 3.2 with H2 (local) / PostgreSQL (prod)
- **Frontend**: Angular 17 with TypeScript
- **Caching**: Redis (optional for local dev)
- **Security**: JWT-based authentication (disabled in local)

## Quick Start

### 1. Backend Setup
```bash
# Start backend with local profile
./start-local.sh
```

### 2. Frontend Setup
```bash
cd frontend
./start-local.sh
```

### 3. Access Points
- **Frontend**: http://localhost:4200
- **Backend API**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/api/health
- **H2 Console**: http://localhost:8080/h2-console
- **API Docs**: http://localhost:8080/swagger-ui.html

## Environment Profiles

### Local Development (`local` profile)
- H2 in-memory database
- Security disabled
- Sample data auto-generated
- Simplified logging

### Production (`default` profile)
- PostgreSQL database
- Full JWT security
- Redis caching
- Comprehensive monitoring

## Data Flow

1. **Frontend** (Angular) → **Backend** (Spring Boot) → **Database** (H2/PostgreSQL)
2. **Mock Data**: Generated automatically for development
3. **Real-time Updates**: WebSocket connections for live data
4. **Caching**: Redis for API response caching (production)

## Troubleshooting

### Backend Won't Start
```bash
# Kill processes on port 8080
lsof -ti:8080 | xargs kill -9

# Check Java version (requires Java 17+)
java -version

# Clean and restart
mvn clean
./start-local.sh
```

### Frontend Issues
```bash
# Clear Angular cache
rm -rf .angular/cache
rm -rf node_modules
npm install

# Restart with specific environment
ng serve --configuration=development
```

### Data Not Loading
1. Check backend health: http://localhost:8080/api/health
2. Verify frontend environment points to localhost
3. Check browser console for CORS errors
4. Ensure H2 database is initialized

## Development Workflow

1. **Local Testing**: Use `local` profile for development
2. **Feature Development**: Create feature branches
3. **Testing**: Run with production profile before deployment
4. **Deployment**: Use Docker containers for production

## Key Files

- `application-local.yml`: Local development configuration
- `environment-local.ts`: Frontend local environment
- `start-local.sh`: Backend startup script
- `frontend/start-local.sh`: Frontend startup script