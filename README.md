# ⚡ Thunder

**Enterprise-grade CTA, Nudge, and Behaviour Tag Management Platform**

[![Documentation](https://img.shields.io/badge/docs-live-brightgreen)](https://dream-horizon-org.github.io/raven-thunder/)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/)
[![Vert.x](https://img.shields.io/badge/Vert.x-4.5-blue)](https://vertx.io/)

## 🌟 Overview

Thunder is a robust, open-source platform built with **Java 17** and **Vert.x** for managing CTAs (Call-to-Actions), Nudges, and Behaviour Tags. It provides a comprehensive suite of APIs for both administrative management and SDK integration, enabling real-time user engagement and behavior-driven experiences.

**📖 [View Full Documentation →](https://dream-horizon-org.github.io/raven-thunder/)**

## Why Thunder?

- 🔐 **Enterprise-Grade**: Built with security and scalability in mind
- ⚡ **Reactive Architecture**: Powered by Vert.x for high-performance, non-blocking I/O
- 🎯 **Flexible Configuration**: No-code/Low-code approach for managing CTAs and Nudges
- 🏢 **Multi-Tenant Ready**: Supports multiple tenants with logical isolation
- 🚀 **Quick Implementation**: Get up and running in minutes with Docker
- 📱 **Real-Time Updates**: Dynamic CTA and Nudge management without app releases
- 🔑 **State Machine Based**: Sophisticated state tracking for user journeys
- 💾 **Aerospike Integration**: High-performance NoSQL database for reactive data access

## 📋 Table of Contents

- [Features](#-features)
- [Getting Started](#-getting-started)
- [Documentation](#-documentation)
- [API Reference](#-api-reference)
- [Configuration](#-configuration)
- [Testing](#-testing)
- [Deployment](#-deployment)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)
- [Community](#-community)
- [License](#-license)

## ✨ Features

### 🎯 Core Entities

- **📢 CTAs (Call-to-Actions)**: User journey management with state machines
- **💬 Nudges**: Real-time, configurable prompts for user guidance
- **🏷️ Behaviour Tags**: Group and control multiple CTAs with frequency management

### 🔌 REST APIs

- **👨‍💼 Admin API** (19 endpoints): Complete CRUD operations for CTAs, Nudges, and Behaviour Tags
- **📱 SDK API** (7 endpoints): Client-facing APIs for CTA decisions and state management
- **🔍 Debug APIs**: Utilities for testing and debugging

### 🏗️ Architecture

- **📦 Multi-Module Design**: Clean separation with `thunder-core`, `thunder-api`, and `thunder-admin`
- **⚡ Reactive Programming**: Built on RxJava3 for non-blocking operations
- **💾 Aerospike Integration**: Reactive data access with automatic seeding and indexing
- **🐳 Docker Ready**: Full Docker Compose setup with all dependencies

### 🛡️ Quality & Reliability

- **✅ Health Checks**: Comprehensive monitoring for services and database connectivity
- **🧪 Testing**: Unit and integration tests with JUnit 5, Mockito, and Testcontainers
- **📏 Code Quality**: Spotless formatting and Checkstyle linting
- **🔒 Security**: Multi-tenant support with tenant-level isolation

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

- ✅ **Multi-module architecture**: `thunder-core`, `thunder-api`, and `thunder-admin`
- ✅ **Complete REST APIs**: Admin panel (19 endpoints) and SDK/Debug APIs (7 endpoints)
- ✅ **Aerospike integration**: Reactive data access with RxJava3
- ✅ **Docker-ready**: Full Docker Compose setup with Aerospike, seed data, and indexes
- ✅ **Health checks**: Comprehensive health monitoring for services and Aerospike
- ✅ **CI & Security**: GitHub Actions CI, release pipelines, and CodeQL code scanning
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

2. **Start Thunder with Docker (Recommended):**

```bash
./scripts/start.sh
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
./scripts/logs.sh
```

5. **Stop Thunder:**

```bash
./scripts/stop.sh
```

**📖 Follow our comprehensive guide on [documentation website](https://dream-horizon-org.github.io/raven-thunder/) to set up Thunder in minutes.**

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

## Code Style

This project uses the [Spotless Maven plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven) with [google-java-format](https://github.com/google/google-java-format) (v1.22.0) to enforce Google Java Style.

Run `mvn spotless:apply` before committing to auto-format code and clean up unused imports / trailing whitespace.

## Testing
### Docker Environment

- **`aerospike.conf`** - Aerospike server configuration for Docker containers
- **Environment variables**: `AEROSPIKE_HOST` overrides default connection settings

**Configuration Flow:**
- **Local development**: Uses `thunder-default.conf` → connects to `localhost`
- **Docker**: Uses `thunder-default.conf` + `AEROSPIKE_HOST` env var → connects to `aerospike` service name

For detailed configuration options, see the [Configuration Guide](https://dream-horizon-org.github.io/raven-thunder/architecture/configuration).

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

Convenience scripts are available in the `scripts/` directory:

- `start.sh` - Build and start Thunder in Docker
- `stop.sh` - Stop Thunder Docker container
- `restart.sh` - Restart Thunder Docker container
- `logs.sh` - View Thunder Docker logs

## Continuous Integration (CI)

We use GitHub Actions in `.github/workflows/ci.yml`:

- Runs on pull requests (opened/reopened/synchronize) and pushes to `main`
- Builds with Java 17 and runs `mvn clean verify` (unit + integration tests)
- Publishes JUnit results back to the PR for quick feedback
- Cancels superseded runs to save time

Security scanning is performed by CodeQL via `.github/workflows/codeql.yml`.

## 📁 Project Structure

```
thunder/
├── thunder-core/              # Core models, DAOs, and client implementations
├── thunder-api/               # SDK and Debug REST APIs (port 8080)
├── thunder-admin/             # Admin panel REST APIs (port 8081)
├── docs/                      # Docusaurus documentation site
├── scripts/
│   ├── start.sh              # Start Thunder in Docker
│   ├── stop.sh               # Stop Thunder Docker container
│   ├── restart.sh            # Restart Thunder Docker container
│   ├── logs.sh               # View Thunder Docker logs
│   └── run-all-seeds.sh      # Executes all AQL seed files
├── aerospike.conf            # Aerospike server config (Docker only)
├── Dockerfile                # Docker build configuration
├── docker-compose.yml         # Docker Compose configuration
└── pom.xml                   # Maven parent POM
```

## 🛠️ Scripts

Convenience scripts are available in the `scripts/` directory:

- **`start.sh`** - Build and start Thunder in Docker
- **`stop.sh`** - Stop Thunder Docker container
- **`restart.sh`** - Restart Thunder Docker container
- **`logs.sh`** - View Thunder Docker logs
- **`run-all-seeds.sh`** - Execute all AQL seed files

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

[Add your license here]

---

**Built with ❤️ by the Thunder team and contributors**

**📖 [View Full Documentation →](https://dream-horizon-org.github.io/raven-thunder/)**
