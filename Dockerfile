FROM eclipse-temurin:21-jdk-alpine AS build

# Install necessary packages for building
RUN apk add --no-cache bash

WORKDIR /app

# Copy gradle files first for better layer caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY src src

# Build the application
RUN ./gradlew build -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Create a non-root user to run the application
RUN addgroup --system --gid 1001 appuser && \
    adduser --system --uid 1001 --ingroup appuser --shell /bin/false appuser

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Set proper ownership
RUN chown -R appuser:appuser /app

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=dev

# Expose the application port
EXPOSE 9000

# Switch to non-root user
USER appuser

# Run the application with security options
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]