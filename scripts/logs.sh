#!/bin/bash

# View Thunder Docker logs

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.." || exit 1

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running"
    exit 1
fi

# Check if container exists
if ! docker ps -a --format '{{.Names}}' | grep -q '^thunder-api$'; then
    echo "❌ Error: Thunder container not found"
    echo "Start it first with: ./scripts/start.sh"
    exit 1
fi

# Follow logs (removed set -e to allow Ctrl+C to exit gracefully)
docker-compose logs -f thunder

