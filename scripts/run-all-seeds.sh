#!/bin/bash

# Script to run all AQL seed files
# This script is executed by the aerospike-seed container

set -e

AEROSPIKE_HOST=${AEROSPIKE_HOST:-aerospike}
AEROSPIKE_PORT=${AEROSPIKE_PORT:-3000}
SEEDS_DIR=${SEEDS_DIR:-/app/seeds}

echo "Running all AQL seed files from $SEEDS_DIR..."

# Find all .aql files and sort them
for file in $(find "$SEEDS_DIR" -name "*.aql" -type f | sort); do
    if [ -f "$file" ]; then
        filename=$(basename "$file")
        echo "Running: $filename"
        aql -h "$AEROSPIKE_HOST" -p "$AEROSPIKE_PORT" \
            --socket-timeout=100000000 \
            --timeout=100000000 \
            -f "$file"
    fi
done

echo "All seed files completed successfully"

