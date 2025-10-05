# Weather Service - Production Deployment

## Architecture
- **Frontend**: Static HTML/JS hosted on AWS S3
- **Backend**: Spring Boot REST API on AWS EC2
- **Database**: PostgreSQL (for user authentication only)
- **Cache**: Redis (for API response caching)
- **External API**: OpenWeatherMap for real-time weather data

## Deployed URLs
- **Frontend**: http://weather-frontend-cr2e0cs5.s3-website-us-east-1.amazonaws.com/
- **Backend API**: http://54.196.61.107:8080
- **Health Check**: http://54.196.61.107:8080/api/weather/health

## API Endpoints
- `POST /api/auth/login` - User authentication
- `POST /api/auth/register` - User registration  
- `GET /api/weather/current/{city}` - Current weather by city
- `GET /api/weather/forecast/{city}` - 5-day forecast by city
- `GET /api/weather/health` - Service health check

## Features
✅ Real-time weather data for 70+ US cities  
✅ Interactive map with clickable markers  
✅ 5-day weather forecasts  
✅ JWT-based authentication  
✅ Auto-login (no manual authentication required)  
✅ Responsive design  
✅ Production logging and monitoring  

## Monthly Cost: ~$8.50
- EC2 t3.micro: ~$7.50
- S3 hosting: ~$1.00

## Monitoring
- Health endpoint: `/api/weather/health`
- Metrics: `/actuator/metrics`
- Logs: `/var/log/weather-service.log`

## Environment Variables
- `OPENWEATHER_API_KEY`: OpenWeatherMap API key
- `SPRING_PROFILES_ACTIVE`: Set to `prod` for production

## Security
- JWT tokens for API authentication
- CORS enabled for frontend domain
- Input validation and sanitization
- Rate limiting via OpenWeatherMap API limits

## Maintenance
- Logs rotate automatically (30 days retention)
- No database maintenance required (stateless weather data)
- Monitor OpenWeatherMap API usage limits