#!/bin/bash

# View Thunder Docker logs

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/../.." || exit 1

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running"
    exit 1
fi

# Check if containers exist
if ! docker ps -a --format '{{.Names}}' | grep -qE '^thunder-(api|admin|aerospike|scalar|cors-proxy)'; then
    echo "❌ Error: Thunder containers not found"
    echo "Start them first with: ./quick-start.sh or ./scripts/docker/start.sh"
    exit 1
fi

# Show logs for specific service or all
case "$1" in
    api)
        docker-compose logs -f thunder-api
        ;;
    admin)
        docker-compose logs -f thunder-admin
        ;;
    aerospike)
        docker-compose logs -f aerospike
        ;;
    seed)
        docker-compose logs aerospike-seed
        ;;
    indexes)
        docker-compose logs aerospike-indexes
        ;;
    scalar)
        docker-compose logs -f scalar
        ;;
    cors)
        docker-compose logs -f cors-proxy
        ;;
    all)
        echo "Following logs for all Thunder services..."
        docker-compose logs -f
        ;;
    *)
        echo "Following logs for API and Admin services..."
        echo "Usage: ./scripts/docker/logs.sh [api|admin|aerospike|seed|indexes|scalar|cors|all]"
        echo ""
        docker-compose logs -f thunder-api thunder-admin
        ;;
esac

