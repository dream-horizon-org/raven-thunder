# Thunder

Thunder is an open-source project built with Java 17 and Vert.x framework for managing CTAs (Call-to-Actions), Nudges, and Behaviour Tags.

## Features

- ✅ **Multi-module architecture**: `thunder-core`, `thunder-api`, and `thunder-admin`
- ✅ **Complete REST APIs**: Admin panel (19 endpoints) and SDK/Debug APIs (7 endpoints)
- ✅ **Aerospike integration**: Reactive data access with RxJava3
- ✅ **Docker-ready**: Full Docker Compose setup with Aerospike, seed data, and indexes
- ✅ **Health checks**: Comprehensive health monitoring for services and Aerospike

## Requirements

- Java 17 (OpenJDK or equivalent)
- Maven 3.6+

## Building

To build the project:

```bash
mvn clean package
```

This will create a fat JAR at `target/thunder-1.0.0-SNAPSHOT-fat.jar`.

## Running

### Using Docker (Recommended)

The easiest way to run Thunder is using Docker:

```bash
# Start the application
./scripts/start.sh

# View logs
./scripts/logs.sh

# Stop the application
./scripts/stop.sh

# Restart the application
./scripts/restart.sh
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
- **Automatic seeding**: Seed data and index creation run on startup
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

- **OpenAPI Spec**: `docs/openapi.yaml` (auto-generated from code)
- **Scalar API Reference**: http://localhost:8082 (Interactive API explorer)

### OpenAPI in Docker

The OpenAPI specification file (`docs/openapi.yaml`) should be generated before starting Docker. Use the local script:

```bash
./scripts/generate-openapi.sh
```

This will:
1. Build `thunder-admin` module
2. Generate OpenAPI spec from JAX-RS annotations using SmallRye OpenAPI
3. Copy it to `docs/openapi.yaml` file
4. Make it available to Scalar API Reference service

The OpenAPI file is served by Scalar at http://localhost:8082.

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

Convenience scripts are available in the `scripts/` directory:

- `start.sh` - Build and start Thunder in Docker
- `stop.sh` - Stop Thunder Docker container
- `restart.sh` - Restart Thunder Docker container
- `logs.sh` - View Thunder Docker logs
- `generate-openapi.sh` - Generate OpenAPI specification from code

## Project Structure

```
thunder-oss/
├── thunder-core/            # Core models, DAOs, and client implementations
├── thunder-api/             # SDK and Debug REST APIs (port 8080)
├── thunder-admin/           # Admin panel REST APIs (port 8081)
├── aerospike.conf           # Aerospike server config (Docker only)
├── scripts/
│   ├── docker/
│   │   └── run-all-seeds.sh  # Executes all AQL seed files
│   ├── start.sh
│   ├── stop.sh
│   ├── restart.sh
│   ├── logs.sh
│   └── generate-openapi.sh  # Generate OpenAPI specification
├── docs/
│   └── openapi.yaml          # OpenAPI specification (auto-generated, served by Scalar)
├── thunder-admin/src/main/resources/seeds/
│   └── 001_seed_meta_set.aql # Seed data for meta_set
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## License

[Add your license here]
