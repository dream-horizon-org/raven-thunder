#!/bin/bash

# Generate OpenAPI specifications from both modules
# This script generates separate OpenAPI files for thunder-api and thunder-admin
# Each service gets its own OpenAPI spec file

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT" || exit 1

echo "📝 Generating OpenAPI specifications..."
echo ""

# Build both modules to generate OpenAPI files
# Using package phase to ensure process-classes phase runs (where OpenAPI plugin executes)
echo "🔨 Building thunder-api module..."
mvn clean package -pl thunder-api -am -DskipTests

echo "🔨 Building thunder-admin module..."
mvn clean package -pl thunder-admin -am -DskipTests

# Check if OpenAPI files were generated (plugin generates openapi.yaml by default)
API_OPENAPI="$PROJECT_ROOT/thunder-api/target/openapi/openapi.yaml"
ADMIN_OPENAPI="$PROJECT_ROOT/thunder-admin/target/openapi/openapi.yaml"

# Output files
API_OUTPUT="$PROJECT_ROOT/docs/thunder-api-openapi.yaml"
ADMIN_OUTPUT="$PROJECT_ROOT/docs/thunder-admin-openapi.yaml"

# Create docs directory if it doesn't exist
mkdir -p "$PROJECT_ROOT/docs"

if [ ! -f "$API_OPENAPI" ]; then
    echo "❌ Error: thunder-api OpenAPI file not found at $API_OPENAPI"
    echo "   Looking for: $API_OPENAPI"
    echo "   Files in directory:"
    ls -la "$PROJECT_ROOT/thunder-api/target/openapi/" 2>/dev/null || echo "   Directory does not exist"
    exit 1
fi

if [ ! -f "$ADMIN_OPENAPI" ]; then
    echo "❌ Error: thunder-admin OpenAPI file not found at $ADMIN_OPENAPI"
    echo "   Looking for: $ADMIN_OPENAPI"
    echo "   Files in directory:"
    ls -la "$PROJECT_ROOT/thunder-admin/target/openapi/" 2>/dev/null || echo "   Directory does not exist"
    exit 1
fi

echo ""
echo "📋 Copying OpenAPI specifications..."

# Copy thunder-api OpenAPI spec
cp "$API_OPENAPI" "$API_OUTPUT"
echo "  ✅ thunder-api: $API_OPENAPI -> $API_OUTPUT"

# Copy thunder-admin OpenAPI spec
cp "$ADMIN_OPENAPI" "$ADMIN_OUTPUT"
echo "  ✅ thunder-admin: $ADMIN_OPENAPI -> $ADMIN_OUTPUT"

echo ""
echo "✅ OpenAPI specifications generated successfully!"
echo ""
echo "📄 Generated files:"
echo "   - $API_OUTPUT (Thunder API - SDK & Debug)"
echo "   - $ADMIN_OUTPUT (Thunder Admin - Management)"
echo ""
echo "💡 Next steps:"
echo "   - Review the generated OpenAPI files"
echo "   - Commit them to the repository"
echo "   - Access Scalar API Reference:"
echo "     • Thunder API: http://localhost:8082"
echo "     • Thunder Admin: http://localhost:8083"

