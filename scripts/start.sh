#!/bin/bash

# Build and run Thunder in Docker container

set -e

echo "⚡ Starting Thunder in Docker..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running"
    echo "Please start Docker Desktop and try again"
    exit 1
fi

# Stop and remove existing container if running
if docker ps -a --format '{{.Names}}' | grep -q '^thunder-api$'; then
    echo "Stopping existing container..."
    docker-compose down
fi

# Build and start container
echo "Building and starting container..."
docker-compose up -d --build

echo ""
echo "✅ Thunder is running in Docker!"
echo ""
echo "🔗 Application: http://localhost:8080"
echo "🏥 Health check: http://localhost:8080/healthcheck"
echo "🏥 Ping:          http://localhost:8080/healthcheck/ping"
echo ""
echo "📋 View logs:    docker-compose logs -f"
echo "📋 View logs:    docker-compose logs -f thunder"
echo "🛑 Stop:         docker-compose down"
echo "🔄 Restart:       docker-compose restart"

