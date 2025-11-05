#!/bin/bash

# Restart Thunder Docker container

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.." || exit 1

echo "🔄 Restarting Thunder in Docker..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running"
    echo "Please start Docker Desktop and try again"
    exit 1
fi

# Check if container exists
if ! docker ps -a --format '{{.Names}}' | grep -q '^thunder-api$'; then
    echo "⚠️  Thunder container not found. Starting it..."
    echo ""
    ./scripts/start.sh
    exit 0
fi

# Restart container
docker-compose restart thunder

echo ""
echo "✅ Thunder restarted successfully!"
echo ""
echo "📋 View logs: ./scripts/logs.sh"

