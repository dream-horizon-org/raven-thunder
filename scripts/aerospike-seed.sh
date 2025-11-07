#!/bin/bash

# Aerospike Seed Data Script
# This script runs all seed AQL files in sequence using aql command

set -e

# Configuration
AEROSPIKE_HOST=${AEROSPIKE_HOST:-aerospike}
AEROSPIKE_PORT=${AEROSPIKE_PORT:-3000}
SOCKET_TIMEOUT=${SOCKET_TIMEOUT:-100000000}
TIMEOUT=${TIMEOUT:-100000000}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to wait for Aerospike to be ready
wait_for_aerospike() {
    print_status "Waiting for Aerospike to be ready..."
    local max_attempts=30
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if aql -h "$AEROSPIKE_HOST" -p "$AEROSPIKE_PORT" \
            --socket-timeout="$SOCKET_TIMEOUT" \
            --timeout="$TIMEOUT" \
            -e "SHOW NAMESPACES" > /dev/null 2>&1; then
            print_success "Aerospike is ready!"
            return 0
        fi
        attempt=$((attempt + 1))
        print_warning "Aerospike is not ready yet. Waiting... (attempt $attempt/$max_attempts)"
        sleep 2
    done
    
    print_error "Aerospike did not become ready after $max_attempts attempts"
    return 1
}

# Function to run AQL files in a directory
run_aql_files() {
    local dir=$1
    local type=$2

    if [ ! -d "$dir" ]; then
        print_warning "$type directory '$dir' does not exist. Skipping..."
        return 0
    fi

    # Find all .aql files and sort them
    local files=($(find "$dir" -name "*.aql" -type f | sort))

    if [ ${#files[@]} -eq 0 ]; then
        print_warning "No .aql files found in $type directory '$dir'. Skipping..."
        return 0
    fi

    print_status "Running $type files from '$dir':"
    for file in "${files[@]}"; do
        local filename=$(basename "$file")
        print_status "  - Executing: $filename"

        if aql -h "$AEROSPIKE_HOST" -p "$AEROSPIKE_PORT" \
            --socket-timeout="$SOCKET_TIMEOUT" \
            --timeout="$TIMEOUT" \
            -f "$file" 2>&1; then
            print_success "  ✓ $filename completed successfully"
        else
            print_error "  ✗ $filename failed"
            exit 1
        fi
    done

    print_success "All $type files completed successfully!"
}

# Main execution
main() {
    print_status "Starting Aerospike seed data process..."

    # Wait for Aerospike to be ready
    if ! wait_for_aerospike; then
        print_error "Failed to connect to Aerospike. Exiting."
        exit 1
    fi

    # Run seeds
    print_status "=== RUNNING SEEDS ==="
    run_aql_files "/app/seeds" "seed"

    print_success "=== ALL OPERATIONS COMPLETED SUCCESSFULLY ==="
}

# Run main function
main "$@"

