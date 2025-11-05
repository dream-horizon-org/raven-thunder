# Multi-stage build for smaller image size

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml files first for better dependency caching
COPY pom.xml .
COPY thunder-core/pom.xml ./thunder-core/
COPY thunder-api/pom.xml ./thunder-api/
COPY thunder-admin/pom.xml ./thunder-admin/

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY thunder-core/src ./thunder-core/src
COPY thunder-api/src ./thunder-api/src
COPY thunder-admin/src ./thunder-admin/src

# Build arguments to specify which module to build
ARG MODULE=thunder-api

# Build the application
RUN mvn clean package -DskipTests -pl ${MODULE} -am

# Stage 2: Create runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

# Build argument to specify which module to copy
ARG MODULE=thunder-api

# Copy the fat JAR from builder stage
COPY --from=builder /app/${MODULE}/target/${MODULE}-*-fat.jar /app/thunder.jar

# Copy all resource files from builder stage
COPY --from=builder /app/${MODULE}/src/main/resources/ /app/

# Create logs directory
RUN mkdir -p /app/logs

# Expose the default port (will be overridden by docker-compose)
EXPOSE 8080

# Set environment variables (can be overridden at runtime)
ENV SERVER_HOST=0.0.0.0
ENV SERVER_PORT=8080
ENV SERVER_INSTANCES=1

# Add health check (port will be overridden by docker-compose)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/healthcheck/ping || exit 1

# Run the application
CMD ["java", "-jar", "/app/thunder.jar"]
