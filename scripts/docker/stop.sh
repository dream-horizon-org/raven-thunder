#!/bin/bash

# Stop Thunder Docker containers

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/../.." || exit 1

echo "🛑 Stopping Thunder in Docker..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running"
    exit 1
fi

# Check if containers are running
if ! docker ps --format '{{.Names}}' | grep -q '^thunder-api$\|^thunder-admin$'; then
    echo "⚠️  Thunder containers are not running"
    echo ""
    # Try to clean up any stopped containers
    if docker ps -a --format '{{.Names}}' | grep -q '^thunder-api$\|^thunder-admin$'; then
        echo "Removing stopped containers..."
        docker-compose down
    fi
    echo "✅ Cleanup complete"
    exit 0
fi

# Stop and remove containers
docker-compose down

echo ""
echo "✅ Thunder stopped successfully!"

