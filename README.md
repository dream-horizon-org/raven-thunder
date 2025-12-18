# ⚡ Thunder

[![Documentation](https://img.shields.io/badge/docs-live-brightgreen)](https://dream-horizon-org.github.io/raven-thunder/)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Vert.x](https://img.shields.io/badge/Vert.x-4.5-blue)](https://vertx.io/)

## 🌟 Overview

Thunder is a robust, open-source platform built with **Java 17** and **Vert.x** for managing User Journeys, Nudges, and Behaviour. It provides a comprehensive suite of APIs for both administrative management and SDK integration, enabling real-time user engagement and behavior-driven experiences.

**📖 [View Full Documentation →](https://dream-horizon-org.github.io/raven-thunder/)**

## Key Features

- 🔐 **Enterprise-Grade**: Built with security and scalability in mind
- ⚡ **Reactive Architecture**: Powered by Vert.x for high-performance, non-blocking I/O
- 🎯 **Flexible Configuration**: No-code/Low-code approach for managing CTAs and Nudges
- 🏢 **Multi-Tenant Ready**: Supports multiple tenants with logical isolation
- 🚀 **Quick Implementation**: Get up and running in minutes with Docker
- 📱 **Real-Time Updates**: Dynamic CTA and Nudge management without app releases
- 🔑 **State Machine Based**: Sophisticated state tracking for user journeys
- 💾 **Aerospike Integration**: High-performance NoSQL database for reactive data access

## 🚀 Getting Started

### Prerequisites

- **Docker** ≥ 20.10 ([Download Docker Desktop](https://www.docker.com/products/docker-desktop))
- **Docker Compose** ≥ 2.0 (Usually included with Docker Desktop)
- **Maven** ≥ 3.6 ([Download Maven](https://maven.apache.org/download.cgi))
- **Java 17** (JDK) ([Download Java 17](https://adoptium.net/))

### Verify Installations

You can verify the installations by running the following commands in your terminal:

```bash
docker --version
mvn --version
java -version
```

**Important:** Ensure that Java 17 is the active version. Maven should also be configured to use Java 17 - you can verify this by checking that `mvn --version` shows Java 17 in its output.

### Port Requirements

Make sure the following ports are free and not in use by other services:

- **8080** – Thunder API service (SDK endpoints)
- **8081** – Thunder Admin service (Admin panel)
- **3000** – Aerospike (default port)

If any of these ports are in use, you'll need to stop the conflicting services or modify the port mappings in `docker-compose.yml`.

### Quick Start

1. **Clone the repository:**

```bash
git clone https://github.com/dream-horizon-org/thunder.git
cd thunder
```

2. **Start Thunder with Docker (Recommended):**

```bash
# Quick Start (recommended)
./quick-start.sh

# Or use individual scripts
./scripts/docker/start.sh    # Start the application
./scripts/docker/logs.sh     # View logs
./scripts/docker/stop.sh     # Stop the application
./scripts/docker/restart.sh  # Restart the application
```

This script will:
- Build the Thunder application using Maven
- Start all required services (Aerospike, Thunder API, Thunder Admin) using Docker Compose
- Run database migrations and seed initial data
- Create necessary indexes

3. **Verify the setup:**

Check health endpoints:

```bash
# Thunder API health check
curl http://localhost:8080/healthcheck

# Thunder Admin health check
curl http://localhost:8081/healthcheck
```

**Expected response:**
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

4. **View logs:**

```bash
./scripts/docker/logs.sh
```

5. **Stop Thunder:**

```bash
./scripts/docker/stop.sh
```

**📖 Follow our comprehensive guide on [documentation website](https://dream-horizon-org.github.io/raven-thunder/) to set up Thunder in minutes.**

### Docker Setup Details

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

### Alternative: Using Docker Compose Directly

```bash
docker-compose up -d --build
```

### Alternative: Running Locally

If you prefer to run without Docker:

```bash
# Build the project
mvn clean package

# Run Admin service
java -jar thunder-admin/target/thunder-admin-1.0.0-SNAPSHOT-fat.jar

# Run API service (in another terminal)
java -jar thunder-api/target/thunder-api-1.0.0-SNAPSHOT-fat.jar
```

**Note:** For local development, you need to:
1. Run Aerospike locally or configure connection to a remote instance
2. Configure Aerospike namespaces (`thunder` and `thunder-admin`) if needed
3. Update configuration files as needed

## 📚 Documentation

**📖 [Full Documentation Site](https://dream-horizon-org.github.io/raven-thunder/)**

Our comprehensive documentation includes:

- **[Getting Started](https://dream-horizon-org.github.io/raven-thunder/getting-started/overview)** - Setup and quickstart guides
- **[Core Entities](https://dream-horizon-org.github.io/raven-thunder/getting-started/core-entities)** - Understanding CTAs, Behaviour Tags, and Nudges
- **[Architecture](https://dream-horizon-org.github.io/raven-thunder/architecture/modules)** - System architecture and design
- **[API Documentation](https://dream-horizon-org.github.io/raven-thunder/api/overview)** - SDK API endpoints and contracts
- **[Admin Documentation](https://dream-horizon-org.github.io/raven-thunder/admin/overview)** - Admin API endpoints and contracts
- **[Operations](https://dream-horizon-org.github.io/raven-thunder/operations/docker)** - Docker, testing, CI/CD, and releases

### Running Documentation Locally

```bash
cd docs
npm install
npm run start
# open http://localhost:3000
```

**Requirements:**
- Node.js 20 (repo includes `.nvmrc`, so you can run `nvm use`)
- Do not commit `docs/node_modules` or `docs/build` (gitignored)

## 🔌 API Reference

### Admin API (Port 8081)

Complete REST API for managing CTAs, Nudges, and Behaviour Tags:

- **CTA Management**: Create, update, list, and manage CTA status
- **Behaviour Tags**: Create and update behaviour tags with frequency rules
- **Nudge Preview**: Create and manage nudge previews
- **Filtering**: Advanced filtering and pagination

**📖 [View Admin API Contracts →](https://dream-horizon-org.github.io/raven-thunder/api/admin-contracts)**

### SDK API (Port 8080)

Client-facing APIs for application integration:

- **App Launch**: Fetch active CTAs and state machines
- **Snapshot Delta**: Update and merge state machine snapshots
- **Nudge Preview**: Retrieve nudge previews by ID

**📖 [View SDK API Contracts →](https://dream-horizon-org.github.io/raven-thunder/api/thunder-api-contracts)**

### API Documentation (OpenAPI)

Thunder provides comprehensive API documentation via OpenAPI 3.0 specification:

- **Thunder API Spec**: `openapi/thunder-api-openapi.yaml` (auto-generated from code)
- **Thunder Admin Spec**: `openapi/thunder-admin-openapi.yaml` (auto-generated from code)
- **Scalar API Reference**: http://localhost:8082
  - Switch between Thunder API and Thunder Admin using the dropdown
  - Thunder API: SDK & Debug endpoints
  - Thunder Admin: Management endpoints (CTAs, Nudges, Behaviour Tags)

### Generating OpenAPI

The OpenAPI specification files are automatically generated during Docker build. You can also generate them manually:

```bash
./scripts/generate-openapi.sh
```

This will:
1. Build both `thunder-api` and `thunder-admin` modules
2. Generate OpenAPI specs from JAX-RS annotations using SmallRye OpenAPI
3. Copy them to `openapi/thunder-api-openapi.yaml` and `openapi/thunder-admin-openapi.yaml`
4. Make them available to Scalar API Reference service

The Scalar service (http://localhost:8082) provides a unified interface to both API specs with a dropdown to switch between them.

### Health Checks

Both services provide comprehensive health check endpoints:

```bash
# Thunder API
curl http://localhost:8080/healthcheck
curl http://localhost:8080/healthcheck/ping

# Thunder Admin
curl http://localhost:8081/healthcheck
curl http://localhost:8081/healthcheck/ping
```

## ⚙️ Configuration

### Local Development

- **`thunder-default.conf`** - Default configuration for local development
- **`thunder.conf`** - Optional local overrides (empty by default)

### Docker Environment

- **`aerospike.conf`** - Aerospike server configuration for Docker containers
- **Environment variables**: `AEROSPIKE_HOST` overrides default connection settings

**Configuration Flow:**
- **Local development**: Uses `thunder-default.conf` → connects to `localhost`
- **Docker**: Uses `thunder-default.conf` + `AEROSPIKE_HOST` env var → connects to `aerospike` service name

For detailed configuration options, see the [Configuration Guide](https://dream-horizon-org.github.io/raven-thunder/architecture/configuration).

## Code Style

This project uses the [Spotless Maven plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven) with [google-java-format](https://github.com/google/google-java-format) (v1.22.0) to enforce Google Java Style.

Run `mvn spotless:apply` before committing to auto-format code and clean up unused imports / trailing whitespace.

## 🧪 Testing

### Run All Tests

```bash
mvn -q test
```

### Run Tests for Specific Module

```bash
mvn -q -pl thunder-admin test
mvn -q -pl thunder-api test
mvn -q -pl thunder-core test
```

### Run a Single Test Class or Method

```bash
# Single class
mvn -q -pl thunder-admin -Dtest=AdminServiceImplCreateCTATest test
# Single method
mvn -q -pl thunder-admin -Dtest=AdminServiceImplStatusTest#updateStatusToLive_transitionsDraftToLive test
```

### Run Integration Tests

```bash
# Requires Docker running
mvn -pl thunder-api -am verify
mvn -pl thunder-admin -am verify
```

### Useful Test Flags

```bash
# Skip integration tests only (Failsafe)
mvn -pl thunder-api -DskipITs=true verify

# Skip all tests
mvn -DskipTests package
```

**Test Stack:**
- JUnit 5 for test framework
- Mockito for mocking
- AssertJ for assertions
- Testcontainers for integration testing

**Notes:**
- Tests use JUnit 5, Mockito, and AssertJ (versions managed in parent `pom.xml`).
- Surefire is configured with `<useModulePath>false</useModulePath>` for Java 17 compatibility.
- Failsafe runs `**/*IT.java` tests during the `verify` phase.

## 🚀 Deployment

Thunder can be deployed using Docker Compose for development or containerized/virtual machine for production environments.

### Building

```bash
mvn clean package
```

This creates fat JARs at:
- `thunder-api/target/thunder-api-1.0.0-SNAPSHOT-fat.jar`
- `thunder-admin/target/thunder-admin-1.0.0-SNAPSHOT-fat.jar`

### Docker Deployment

```bash
# Build Docker image
docker build -t thunder:latest .

# Using Docker Compose
docker-compose up -d --build
```

For detailed deployment instructions and production best practices, see the [Deployment Guide](https://dream-horizon-org.github.io/raven-thunder/operations/docker).

## 📁 Project Structure

```
thunder/
├── thunder-core/              # Core models, DAOs, and client implementations
├── thunder-api/               # SDK and Debug REST APIs (port 8080)
├── thunder-admin/             # Admin panel REST APIs (port 8081)
├── docs/                      # Docusaurus documentation site
├── openapi/                   # OpenAPI specification files (auto-generated)
│   ├── thunder-api-openapi.yaml
│   └── thunder-admin-openapi.yaml
├── scripts/
│   ├── docker/
│   │   ├── run-all-seeds.sh  # Executes all AQL seed files
│   │   ├── start.sh          # Start Docker services
│   │   ├── stop.sh            # Stop Docker services
│   │   ├── restart.sh         # Restart Docker services
│   │   └── logs.sh            # View Docker logs
│   └── generate-openapi.sh    # Generate OpenAPI specification
├── quick-start.sh             # Quick start script (recommended entry point)
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

## 🛠️ Scripts

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

## Continuous Integration (CI)

We use GitHub Actions in `.github/workflows/ci.yml`:

- Runs on pull requests (opened/reopened/synchronize) and pushes to `main`
- Builds with Java 17 and runs `mvn clean verify` (unit + integration tests)
- Publishes JUnit results back to the PR for quick feedback
- Cancels superseded runs to save time

Security scanning is performed by CodeQL via `.github/workflows/codeql.yml`.

## 🤝 Contributing

We welcome contributions! Please see our contributing guidelines:

- Follow the code formatting standards (Spotless)
- Ensure all tests pass
- Follow conventional commit format
- Update documentation as needed

**Code Quality Tools:**
- **Spotless**: Code formatting (Google Java Format)
- **Checkstyle**: Code style checking
- Run `mvn spotless:apply` to format code before committing

## 👥 Community

- **💬 [GitHub Discussions](https://github.com/dream-horizon-org/thunder/discussions)** - Ask questions and share ideas
- **🐛 [Issue Tracker](https://github.com/dream-horizon-org/thunder/issues)** - Report bugs and request features
- **📖 [Documentation](https://dream-horizon-org.github.io/raven-thunder/)** - Comprehensive guides and API reference

## 📄 License

Thunder is licensed under the **Apache License 2.0**.

See the [LICENSE](LICENSE) file for details.

---

**Built with ❤️ by Horizon Engineering**

**📖 [View Full Documentation →](https://dream-horizon-org.github.io/raven-thunder/)**
