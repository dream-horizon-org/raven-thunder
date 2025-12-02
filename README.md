# Thunder

Thunder is an open-source project built with Java 17 and Vert.x framework for managing CTAs (Call-to-Actions), Nudges, and Behaviour Tags.

## Features

- ✅ **Multi-module architecture**: `thunder-core`, `thunder-api`, and `thunder-admin`
- ✅ **Complete REST APIs**: Admin panel (19 endpoints) and SDK/Debug APIs (7 endpoints)
- ✅ **Aerospike integration**: Reactive data access with RxJava3
- ✅ **Docker-ready**: Full Docker Compose setup with Aerospike, seed data, and indexes
- ✅ **Health checks**: Comprehensive health monitoring for services and Aerospike

## Documentation (Docusaurus)

Run the docs site locally:

```bash
cd docs
npm install
npm run start
# open http://localhost:3000
```

Requirements:
- Node.js 18 or 20 (repo includes `.nvmrc`, so you can run `nvm use`)
- Do not commit `docs/node_modules` or `docs/build` (gitignored).

## Requirements

- Java 17 (OpenJDK or equivalent)
- Maven 3.6+

## Building

To build the project:

```bash
mvn clean package
```

This will create a fat JAR at `target/thunder-1.0.0-SNAPSHOT-fat.jar`.

## Testing

Run all unit tests from the project root:

```bash
mvn -q test
```

Run tests for a specific module:

```bash
mvn -q -pl thunder-admin test
mvn -q -pl thunder-api test
mvn -q -pl thunder-core test
```

Run a single test class or method:

```bash
# Single class
mvn -q -pl thunder-admin -Dtest=AdminServiceImplCreateCTATest test
# Single method
mvn -q -pl thunder-admin -Dtest=AdminServiceImplStatusTest#updateStatusToLive_transitionsDraftToLive test
```

Integration tests (thunder-api uses Testcontainers for Aerospike):

```bash
# Requires Docker running
mvn -pl thunder-api -am verify
```

Integration tests (thunder-admin, if present):

```bash
# Requires Docker running (only if admin ITs use containers)
mvn -pl thunder-admin -am verify
```

Useful flags:

```bash
# Skip integration tests only (Failsafe)
mvn -pl thunder-api -DskipITs=true verify
# Skip all tests
mvn -DskipTests package
```

Notes:
- Tests use JUnit 5, Mockito, and AssertJ (versions managed in parent `pom.xml`).
- Surefire is configured with `<useModulePath>false</useModulePath>` for Java 17 compatibility.
- Failsafe runs `**/*IT.java` tests during the `verify` phase.

## Running

### Using Docker (Recommended)

The easiest way to run Thunder is using Docker:

```bash
# Quick Start (recommended)
./quick-start.sh

# Or use individual scripts
./scripts/docker/start.sh    # Start the application
./scripts/docker/logs.sh     # View logs
./scripts/docker/stop.sh     # Stop the application
./scripts/docker/restart.sh  # Restart the application
```

Or using docker-compose directly:

```bash
docker-compose up -d --build
```

Docker setup includes:
- **thunder-api**: REST API service (port 8080) for SDK and Debug endpoints
- **thunder-admin**: Admin panel service (port 8081) for CTA, Nudge, and Behaviour Tag management
- **Aerospike**: Database with namespaces `thunder` and `thunder-admin`
- **Scalar API Reference**: Interactive API documentation (port 8082)
- **CORS Proxy**: Nginx proxy for CORS headers (ports 8080, 8081)
- **Automatic seeding**: Seed data and index creation run on startup
  - Seed data includes: CTAs (101, 202), Behaviour Tags (onboarding_eligible), Nudge Preview (5), User State Machine (12345)
  - All seed data uses `tenant1` and matches API documentation examples
- Configuration via environment variables (AEROSPIKE_HOST) and `aerospike.conf` (Aerospike server)

### Running Locally

To run the application locally:

```bash
java -jar target/thunder-1.0.0-SNAPSHOT-fat.jar
```

Or using Maven:

```bash
mvn exec:java -Dexec.mainClass="com.dream11.thunder.Main"
```

**Note:** For local development, you need to:
1. Run Aerospike locally or configure connection to a remote instance
2. Configure Aerospike namespaces (`thunder` and `thunder-admin`) if needed
3. Update `thunder.conf` to override any settings from `thunder-default.conf`

## Configuration

### Local Development
- **`src/main/resources/thunder-default.conf`** - Default configuration for local development
- **`src/main/resources/thunder.conf`** - Optional local overrides (empty by default)

### Docker Environment
- **`aerospike.conf`** - Aerospike server configuration for Docker containers
- Environment variable `AEROSPIKE_HOST` overrides `aerospike.host` (defaults to `localhost`, set to `aerospike` in Docker)

Configuration:
- Local development: Uses `thunder-default.conf` → connects to `localhost`
- Docker: Uses `thunder-default.conf` + `AEROSPIKE_HOST` env var → connects to `aerospike` service name
- Aerospike server config (`aerospike.conf`) is Docker-only

## Health Check

Both services provide health check endpoints:

### Thunder API (Port 8080)
```bash
curl http://localhost:8080/healthcheck
curl http://localhost:8080/healthcheck/ping
```

### Thunder Admin (Port 8081)
```bash
curl http://localhost:8081/healthcheck
curl http://localhost:8081/healthcheck/ping
```

The healthcheck endpoint includes:
- Overall service status
- Aerospike connection status
- Individual namespace statuses (`thunder` and `thunder-admin`)

Example response:
```json
{
  "status": "UP",
  "service": "thunder-api",
  "aerospike": {
    "status": "UP",
    "namespaces": {
      "thunder": "UP",
      "thunder-admin": "UP"
    }
  }
}
```

## Docker

### Building Docker Image

```bash
docker build -t thunder:latest .
```

### Using Docker Compose

```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f thunder

# Stop services
docker-compose down
```

**Note:** If you experience timeout issues during image pulls (e.g., "TLS handshake timeout"), you can increase the timeout:
```bash
export COMPOSE_HTTP_TIMEOUT=300
docker-compose up -d --build
```

For persistent network issues, configure Docker daemon timeouts in `/etc/docker/daemon.json` (or Docker Desktop settings).

## API Documentation

Thunder provides comprehensive API documentation via OpenAPI 3.0 specification:

- **Thunder API Spec**: `docs/thunder-api-openapi.yaml` (auto-generated from code)
- **Thunder Admin Spec**: `docs/thunder-admin-openapi.yaml` (auto-generated from code)
- **Scalar API Reference**: http://localhost:8082
  - Switch between Thunder API and Thunder Admin using the dropdown
  - Thunder API: SDK & Debug endpoints
  - Thunder Admin: Management endpoints (CTAs, Nudges, Behaviour Tags)

### OpenAPI in Docker

The OpenAPI specification files are automatically generated during Docker build. You can also generate them manually:

```bash
./scripts/generate-openapi.sh
```

This will:
1. Build both `thunder-api` and `thunder-admin` modules
2. Generate OpenAPI specs from JAX-RS annotations using SmallRye OpenAPI
3. Copy them to `docs/thunder-api-openapi.yaml` and `docs/thunder-admin-openapi.yaml`
4. Make them available to Scalar API Reference service

The Scalar service (http://localhost:8082) provides a unified interface to both API specs with a dropdown to switch between them.

### Generating OpenAPI Locally

To generate or update the OpenAPI specification locally (outside Docker):

```bash
./scripts/generate-openapi.sh
```

This script performs the same steps as the Docker service, useful for:
- Previewing changes before committing
- Generating the spec for CI/CD pipelines
- Manual updates when needed

## Scripts

### Quick Start

The easiest way to get started:

```bash
./quick-start.sh
```

This will start all Thunder services in Docker with seed data.

### Docker Scripts

Docker-related scripts are in `scripts/docker/`:

- `scripts/docker/start.sh` - Build and start Thunder in Docker (includes seed data and indexes)
- `scripts/docker/stop.sh` - Stop Thunder Docker containers and clean up
- `scripts/docker/restart.sh` - Restart Thunder Docker containers
- `scripts/docker/logs.sh` - View Thunder Docker logs
  - Usage: `./scripts/docker/logs.sh [api|admin|aerospike|seed|indexes|scalar|cors|all]`
  - Default: Shows API and Admin logs
  - Examples:
    - `./scripts/docker/logs.sh api` - View only API logs
    - `./scripts/docker/logs.sh seed` - View seed data execution logs
    - `./scripts/docker/logs.sh all` - View all service logs

### Development Scripts

- `scripts/generate-openapi.sh` - Generate OpenAPI specification from code

## Project Structure

```
thunder-oss/
├── thunder-core/            # Core models, DAOs, and client implementations
├── thunder-api/             # SDK and Debug REST APIs (port 8080)
├── thunder-admin/           # Admin panel REST APIs (port 8081)
├── aerospike.conf           # Aerospike server config (Docker only)
├── scripts/
│   ├── docker/
│   │   ├── run-all-seeds.sh  # Executes all AQL seed files
│   │   ├── start.sh          # Start Docker services
│   │   ├── stop.sh            # Stop Docker services
│   │   ├── restart.sh         # Restart Docker services
│   │   └── logs.sh            # View Docker logs
│   └── generate-openapi.sh    # Generate OpenAPI specification
├── quick-start.sh             # Quick start script (recommended entry point)
├── docs/
│   ├── thunder-api-openapi.yaml    # Thunder API OpenAPI spec (auto-generated)
│   └── thunder-admin-openapi.yaml  # Thunder Admin OpenAPI spec (auto-generated)
├── thunder-admin/src/main/resources/seeds/
│   ├── 001_seed_meta_set.aql          # Seed data for meta_set
│   ├── 002_seed_cta.aql               # Seed data for CTAs (101, 202)
│   ├── 003_seed_behaviour_tag.aql     # Seed data for Behaviour Tags (onboarding_eligible)
│   ├── 004_seed_nudge_preview.aql     # Seed data for Nudge Preview (5)
│   └── 005_seed_user_state_machine.aql # Seed data for User State Machine (12345)
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## License

[Add your license here]
