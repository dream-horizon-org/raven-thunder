#!/bin/bash

# View Thunder Docker logs

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/.." || exit 1

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running"
    exit 1
fi

# Check if containers exist
if ! docker ps -a --format '{{.Names}}' | grep -q '^thunder-api$\|^thunder-admin$'; then
    echo "❌ Error: Thunder containers not found"
    echo "Start them first with: ./scripts/start.sh"
    exit 1
fi

# Show logs for both services
if [ "$1" = "api" ]; then
    docker-compose logs -f thunder-api
elif [ "$1" = "admin" ]; then
    docker-compose logs -f thunder-admin
else
    echo "Following logs for both services (use 'api' or 'admin' to filter)..."
    docker-compose logs -f thunder-api thunder-admin
fi

