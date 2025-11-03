# Thunder

Thunder is an open-source project built with Java 17 and Vert.x framework.

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

### Running Locally

To run the application locally:

```bash
java -jar target/thunder-1.0.0-SNAPSHOT-fat.jar
```

Or using Maven:

```bash
mvn exec:java -Dexec.mainClass="com.dream11.thunder.Main"
```

## Configuration

The application configuration is loaded from `src/main/resources/thunder-default.conf`.

Default configuration:
- Port: 8080
- Instances: 1

## Health Check

The application provides a health check endpoint:

```bash
curl http://localhost:8080/healthcheck
curl http://localhost:8080/healthcheck/ping
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
│   │       ├── thunder-default.conf
│   │       └── logback.xml
│   └── test/
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## License

[Add your license here]
