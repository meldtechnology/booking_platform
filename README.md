# Booking Platform

A reactive Spring Boot application for managing catalog items and user services. It provides a foundation for building a scalable booking system with reactive programming principles.

## Tech Stack
- **Java 21**: Core programming language
- **Spring Boot 3.5.4**: Application framework
- **Spring WebFlux**: Reactive web framework
- **Spring Data R2DBC**: Reactive database access
- **PostgreSQL**: Database system
- **Liquibase**: Database migration tool
- **Lombok**: Reduces boilerplate code
- **Gradle**: Build and dependency management

## Running with Docker

### Using Docker Compose (Recommended)

The easiest way to run the application is using Docker Compose:

```bash
docker-compose up
```

This will start both the application and a PostgreSQL database.

### Using Docker Only

If you want to run just the application container:

1. Build the Docker image:
   ```bash
   docker build -t booking-platform .
   ```

2. Run the container:
   ```bash
   docker run -p 9000:9000 \
     -e DB_HOST=your-db-host \
     -e DB_PORT=5432 \
     -e DB_USERNAME=postgres \
     -e DB_PASSWORD=postgres \
     -e API_KEY=your-api-key \
     booking-platform
   ```

## Environment Variables

The application uses the following environment variables:

- `DB_HOST`: PostgreSQL database host (default: localhost)
- `DB_PORT`: PostgreSQL database port (default: 5432)
- `DB_USERNAME`: PostgreSQL username (default: postgres)
- `DB_PASSWORD`: PostgreSQL password (default: postgres)
- `API_KEY`: API key for authentication (default: default-api-key)
- `SPRING_PROFILES_ACTIVE`: Spring profile to activate (default: dev)

## API Access

Once running, the application is accessible at:

- API: http://localhost:9000
- OpenAPI Documentation: http://localhost:9000/swagger-ui.html