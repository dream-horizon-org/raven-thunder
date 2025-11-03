# Multi-stage build for smaller image size

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first for better dependency caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the fat JAR from builder stage
COPY --from=builder /app/target/thunder-*-fat.jar /app/thunder.jar

# Create logs directory
RUN mkdir -p /app/logs

# Expose the default port
EXPOSE 8080

# Set environment variables (can be overridden at runtime)
ENV SERVER_HOST=0.0.0.0
ENV SERVER_PORT=8080
ENV SERVER_INSTANCES=1

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/healthcheck/ping || exit 1

# Run the application
CMD ["java", "-jar", "/app/thunder.jar"]

