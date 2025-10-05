# Weather Prediction Microservice

A complete weather prediction microservice built with Spring Boot backend and Angular frontend.

## Features

- **Current Weather**: Get real-time weather data by city name or coordinates
- **Weather History**: View historical weather data for any city
- **Caching**: Redis-based caching for improved performance
- **Database**: PostgreSQL for persistent data storage
- **API Documentation**: Swagger/OpenAPI documentation
- **Containerized**: Full Docker support with Docker Compose

## Tech Stack

### Backend
- Spring Boot 3.2
- PostgreSQL with Hibernate/JPA
- Redis for caching
- OpenWeatherMap API integration
- Swagger/OpenAPI documentation

### Frontend
- Angular 17
- TypeScript
- Responsive design

## Quick Start

### Prerequisites
- Docker and Docker Compose
- OpenWeatherMap API key (get from https://openweathermap.org/api)

### Running with Docker Compose

1. Clone the repository
2. Set your OpenWeatherMap API key:
   ```bash
   export OPENWEATHER_API_KEY=your_api_key_here
   ```

3. Build and run the services:
   ```bash
   # Build the backend
   mvn clean package
   
   # Start all services
   docker-compose up -d
   ```

4. Access the application:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html

### API Endpoints

- `GET /api/weather/current/{city}` - Get current weather by city
- `GET /api/weather/current?lat={lat}&lon={lon}` - Get weather by coordinates
- `GET /api/weather/history/{city}?days={days}` - Get weather history
- `GET /api/weather/health` - Health check

### Development

#### Backend Development
```bash
# Run PostgreSQL and Redis
docker-compose up postgres redis -d

# Run Spring Boot application
mvn spring-boot:run
```

#### Frontend Development
```bash
cd frontend
npm install
npm start
```

## Configuration

### Environment Variables
- `OPENWEATHER_API_KEY`: Your OpenWeatherMap API key
- `SPRING_DATASOURCE_URL`: PostgreSQL connection URL
- `SPRING_REDIS_HOST`: Redis host

### Database Schema
The application automatically creates the required tables:
- `weather_data`: Current weather records
- `weather_forecast`: Forecast data

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Angular UI    │────│  Spring Boot    │────│   PostgreSQL    │
│   (Frontend)    │    │   (Backend)     │    │   (Database)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                       ┌─────────────────┐
                       │      Redis      │
                       │    (Cache)      │
                       └─────────────────┘
                              │
                       ┌─────────────────┐
                       │ OpenWeatherMap  │
                       │      API        │
                       └─────────────────┘
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

MIT License