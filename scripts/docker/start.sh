#!/bin/bash

# Build and run Thunder in Docker containers

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/../.." || exit 1

echo "⚡ Starting Thunder in Docker..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running"
    echo "Please start Docker Desktop and try again"
    exit 1
fi

# Stop and remove existing containers if running
if docker ps -a --format '{{.Names}}' | grep -q '^thunder-api$\|^thunder-admin$'; then
    echo "Stopping existing containers..."
    docker-compose down
fi

# Build and start containers
echo "Building and starting containers..."
# Increase timeout for slow network connections (standard docker-compose environment variable)
export COMPOSE_HTTP_TIMEOUT=300
docker-compose up -d --build

echo ""
echo "✅ Thunder is running in Docker!"
echo ""
echo "🔗 Thunder API:      http://localhost:8080"
echo "🏥 API Health check: http://localhost:8080/healthcheck"
echo "🏥 API Ping:         http://localhost:8080/healthcheck/ping"
echo ""
echo "🔗 Thunder Admin:    http://localhost:8081"
echo "🏥 Admin Health check: http://localhost:8081/healthcheck"
echo "🏥 Admin Ping:        http://localhost:8081/healthcheck/ping"
echo ""
echo "📚 API Documentation:"
echo "   📖 Scalar API Reference: http://localhost:8082"
echo "      (Switch between Thunder API and Thunder Admin using dropdown)"
echo ""
echo "📋 View logs:    ./scripts/docker/logs.sh"
echo "🛑 Stop:         ./scripts/docker/stop.sh"
echo "🔄 Restart:      ./scripts/docker/restart.sh"
echo ""
echo "💡 Quick Start:  ./quick-start.sh"

