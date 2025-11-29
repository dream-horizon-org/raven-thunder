#!/bin/bash

# Generate OpenAPI specification from thunder-admin module
# This script generates OpenAPI file from thunder-admin and outputs it to docs/openapi.yaml

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT" || exit 1

echo "📝 Generating OpenAPI specifications..."
echo ""

# Build thunder-admin module to generate OpenAPI file
echo "🔨 Building thunder-admin module..."
mvn clean compile -pl thunder-admin -am -DskipTests

# Check if OpenAPI file was generated (plugin generates openapi.yaml by default)
ADMIN_OPENAPI="$PROJECT_ROOT/thunder-admin/target/openapi/openapi.yaml"
OUTPUT_FILE="$PROJECT_ROOT/docs/openapi.yaml"

# Create docs directory if it doesn't exist
mkdir -p "$PROJECT_ROOT/docs"

if [ ! -f "$ADMIN_OPENAPI" ]; then
    echo "❌ Error: thunder-admin OpenAPI file not found at $ADMIN_OPENAPI"
    echo "   Looking for: $ADMIN_OPENAPI"
    echo "   Files in directory:"
    ls -la "$PROJECT_ROOT/thunder-admin/target/openapi/" 2>/dev/null || echo "   Directory does not exist"
    exit 1
fi

echo ""
echo "📋 Copying OpenAPI specification..."

# Copy thunder-admin OpenAPI spec to docs/
cp "$ADMIN_OPENAPI" "$OUTPUT_FILE"

if [ $? -ne 0 ]; then
    echo "⚠️  Failed to copy OpenAPI file"
    exit 1
fi

echo ""
echo "✅ OpenAPI specification generated successfully!"
echo "📄 Output file: $OUTPUT_FILE"
echo ""
echo "💡 Next steps:"
echo "   - Review the generated docs/openapi.yaml"
echo "   - Commit it to the repository"
echo "   - Access Scalar API Reference at http://localhost:8082"

