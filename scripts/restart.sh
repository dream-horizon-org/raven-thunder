#!/bin/bash

# Restart Thunder Docker container

set -e

echo "🔄 Restarting Thunder in Docker..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running"
    echo "Please start Docker Desktop and try again"
    exit 1
fi

# Restart container
docker-compose restart thunder

echo ""
echo "✅ Thunder restarted successfully!"
echo ""
echo "📋 View logs: docker-compose logs -f thunder"

