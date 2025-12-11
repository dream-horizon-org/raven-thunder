#!/bin/bash

# Quick Start Script for Thunder
# This script provides a convenient entry point to start Thunder

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR" || exit 1

echo "⚡ Thunder Quick Start"
echo ""
echo "This script will start Thunder in Docker with all services."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker is not running"
    echo "Please start Docker Desktop and try again"
    exit 1
fi

# Run the start script
./scripts/docker/start.sh

