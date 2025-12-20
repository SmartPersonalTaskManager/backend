# SPTM Backend

Smart Personal Task Manager - Backend API

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose (for PostgreSQL and Keycloak)

### 1. Start Dependencies

```bash
# Start PostgreSQL and Keycloak
docker-compose up -d

# Verify containers are running
docker ps
```

### 2. Configure Environment

Create a `.env` file or set environment variables:

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sptmdb
DB_USER=sptmuser
DB_PASSWORD=sptmpassword

# JWT Configuration
JWT_SECRET=YourSecureJWTSecretKeyHere
JWT_EXPIRATION_MS=86400000

# Google Calendar (Optional)
GOOGLE_CLIENT_ID=your-client-id
GOOGLE_CLIENT_SECRET=your-client-secret
```

### 3. Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## Docker Setup

The `docker-compose.yaml` includes:

- **PostgreSQL** (port 5432) - Main database
- **Keycloak** (port 8180) - Authentication server

### Keycloak Access

- URL: `http://localhost:8180`
- Admin Username: `admin`
- Admin Password: `admin`

## Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/sptm/backend/
│   │   │   ├── config/          # Security, CORS, etc.
│   │   │   ├── controller/      # REST endpoints
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── model/           # JPA entities
│   │   │   ├── repository/      # Database repositories
│   │   │   ├── service/         # Business logic
│   │   │   └── util/            # Utilities
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Unit tests
├── docker-compose.yaml
├── pom.xml
└── README.md
```

## Configuration

### application.properties

The default configuration uses environment variables for flexibility:

```properties
# Server
server.port=${SERVER_PORT:8080}

# Database
spring.datasource.url=jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:sptmdb}
spring.datasource.username=${DB_USER:sptmuser}
spring.datasource.password=${DB_PASSWORD:sptmpassword}

# JWT
sptm.app.jwtSecret=${JWT_SECRET:default-secret-change-in-production}
sptm.app.jwtExpirationMs=${JWT_EXPIRATION_MS:86400000}
```


## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login

### Tasks
- `GET /api/tasks/user/{userId}` - Get user tasks
- `POST /api/tasks` - Create task
- `PUT /api/tasks/{taskId}` - Update task
- `DELETE /api/tasks/{taskId}` - Delete task

### Missions
- `GET /api/missions/user/{userId}` - Get user missions
- `POST /api/missions` - Create mission
- `PUT /api/missions/{missionId}` - Update mission
- `DELETE /api/missions/{missionId}` - Delete mission
- `POST /api/missions/{missionId}/submissions` - Add sub-mission
- `DELETE /api/missions/submissions/{subMissionId}` - Delete sub-mission

### Analytics
- `GET /api/analytics/weekly/{userId}` - Get weekly statistics

## Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Development

### Database Access

```bash
# Connect to PostgreSQL
docker exec -it sptm-postgres psql -U sptmuser -d sptmdb

# List tables
\dt

# View table structure
\d tasks
```

### Hot Reload

Use Spring Boot DevTools for automatic restart during development.

## Production Build

```bash
# Create production JAR
mvn clean package -DskipTests

# Run the JAR
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## Troubleshooting

### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

### Database Connection Failed
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Restart PostgreSQL
docker-compose restart postgres
```

## License

This project is part of the SPTM (Smart Personal Task Manager) system.

## Contributors

- Backend Team

---

For frontend integration, see the [Frontend README](../frontend/README.md)
