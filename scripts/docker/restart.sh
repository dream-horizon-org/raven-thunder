#!/bin/bash

# Restart Thunder Docker containers

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/../.." || exit 1

echo "🔄 Restarting Thunder in Docker..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running"
    echo "Please start Docker Desktop and try again"
    exit 1
fi

# Check if containers exist
if ! docker ps -a --format '{{.Names}}' | grep -qE '^thunder-(api|admin)'; then
    echo "⚠️  Thunder containers not found. Starting them..."
    echo ""
    ./scripts/docker/start.sh
    exit 0
fi

# Restart containers (restart will also restart dependent services)
docker-compose restart thunder-api thunder-admin

echo ""
echo "✅ Thunder restarted successfully!"
echo ""
echo "📋 View logs: ./scripts/docker/logs.sh"

