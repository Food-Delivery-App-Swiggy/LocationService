# Multi-stage Dockerfile for Gradle-based Spring Boot LocationService
# Stage 1: Build the JAR using Gradle
FROM gradle:8.5-jdk17-alpine AS builder
WORKDIR /app

# Cache dependency layers first
COPY build.gradle settings.gradle gradle.properties* ./
COPY gradle ./gradle
COPY gradlew ./
RUN ./gradlew dependencies --no-daemon || true

# Copy source code and build bootable JAR
COPY src ./src
RUN ./gradlew clean bootJar --no-daemon -x test

# Stage 2: Lean runtime container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Run as a dedicated non-root application user
#RUN addgroup -S appgroup && adduser -S appuser -G appgroup
#USER appuser

# Copy generated JAR from builder stage
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar
# If your project uses non-snapshot versioning, fallback to:
# COPY --from=builder /app/build/libs/*.jar app.jar

# LocationService port (update if configured differently in application.yml, e.g., 8082)
EXPOSE 9092

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]