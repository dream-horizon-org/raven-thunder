#!/bin/bash

# Stop Thunder Docker container

set -e

echo "🛑 Stopping Thunder in Docker..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running"
    exit 1
fi

# Stop and remove container
docker-compose down

echo ""
echo "✅ Thunder stopped successfully!"

