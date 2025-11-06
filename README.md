# Thunder

Thunder is an open-source project built with Java 17 and Vert.x framework.

## Migration Status

This project is currently being migrated from an internal Thunder repository. For detailed migration status, see [MIGRATION_STATUS.md](./MIGRATION_STATUS.md).

**Current Status**:
- ✅ All core models and interfaces (24 files)
- ✅ Aerospike client with reactive RxJava3 operations
- ✅ **ALL repository implementations complete** (BehaviourTag, Nudge, NudgePreview, CTA)
- ✅ Complete data access layer (62 Java files, ~5,000+ LOC)
- ⏳ API and Admin controllers & services (next phase)

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
- Thunder application container
- Aerospike database container with namespaces: `thunder` and `thunder-admin`
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

The application provides a health check endpoint:

```bash
curl http://localhost:8080/healthcheck
curl http://localhost:8080/healthcheck/ping
```

The healthcheck endpoint includes:
- Overall service status
- Aerospike connection status
- Individual namespace statuses (`thunder` and `thunder-admin`)

Example response:
```json
{
  "status": "UP",
  "service": "thunder",
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

## Scripts

Convenience scripts are available in the `scripts/` directory:

- `start.sh` - Build and start Thunder in Docker
- `stop.sh` - Stop Thunder Docker container
- `restart.sh` - Restart Thunder Docker container
- `logs.sh` - View Thunder Docker logs

## Project Structure

```
thunder-oss/
├── aerospike.conf           # Aerospike server config (Docker only)
├── scripts/
│   ├── start.sh
│   ├── stop.sh
│   ├── restart.sh
│   └── logs.sh
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/dream11/thunder/
│   │   │       ├── Main.java
│   │   │       ├── injection/
│   │   │       ├── rest/
│   │   │       ├── util/
│   │   │       └── verticle/
│   │   └── resources/
│   │       ├── thunder-default.conf  # Default config (local dev)
│   │       ├── thunder.conf          # Local overrides (optional)
│   │       └── logback.xml
│   └── test/
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## License

[Add your license here]
