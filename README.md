# GitHub Repository Cache Service

A simple REST service for fetching GitHub repository details and caching the response in a database.

## Features

- Fetch GitHub repository details via REST API
- Automatically cache responses to database (H2 in-memory database)
- Support cache hits to return directly from database, reducing GitHub API calls
- Complete end-to-end tests

## API Endpoints

### GET /repositories/{owner}/{repository-name}

Get detailed information about a specified GitHub repository.

**Path Parameters:**
- `owner`: Repository owner (e.g., `spring-projects`)
- `repository-name`: Repository name (e.g., `spring-boot`)

**Response Example:**
```json
{
    "fullName": "spring-projects/spring-boot",
    "description": "Spring Boot is an open source Java-based framework...",
    "cloneUrl": "https://github.com/spring-projects/spring-boot.git",
    "stars": 50000,
    "createdAt": "2020-01-01T00:00:00"
}
```

**Status Codes:**
- `200 OK`: Successfully returned repository information
- `404 Not Found`: Repository does not exist
- `500 Internal Server Error`: Internal server error

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (in-memory database)
- **Maven** (build tool)
- **JUnit 5** (testing framework)
- **Lombok** (reducing boilerplate code)

## Project Structure

```
src/
├── main/
│   ├── java/com/github/xqiii/cache/
│   │   ├── controller/          # REST controllers
│   │   ├── service/             # Business logic layer
│   │   ├── repository/          # Data access layer
│   │   ├── entity/              # Database entities
│   │   └── dto/                 # Data transfer objects
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/github/xqiii/cache/
        ├── controller/          # Controller unit tests
        ├── integration/         # Integration tests
        └── e2e/                 # End-to-end tests
```

## Build and Run

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Build Project

```bash
mvn clean install
```

### Run Application

```bash
mvn spring-boot:run
```

The application will start at `http://localhost:8080`.

### Run Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=RepositoryE2ETest
```

## Usage Examples

### Using curl

```bash
# Get details for spring-projects/spring-boot
curl http://localhost:8080/repositories/spring-projects/spring-boot
```

### Using Browser

Visit: `http://localhost:8080/repositories/spring-projects/spring-boot`

## Configuration

Configuration file is located at `src/main/resources/application.properties`:

- `server.port`: Server port (default: 8080)
- `github.api.base-url`: GitHub API base URL (default: https://api.github.com)
- `spring.datasource.url`: Database connection URL

## Database

The project uses H2 in-memory database. You can access the H2 console via:
`http://localhost:8080/h2-console`

Connection Information:
- JDBC URL: `jdbc:h2:mem:github_repo_cache`
- Username: `sa`
- Password: (empty)

## Testing

The project includes three types of tests:

1. **Unit Tests** (`RepositoryControllerTest`): Tests the controller layer
2. **Integration Tests** (`RepositoryIntegrationTest`): Tests the service layer and caching logic
3. **End-to-End Tests** (`RepositoryE2ETest`): Tests the complete request-response flow

## Design Notes

- **Layered Architecture**: Uses Controller-Service-Repository layered architecture
- **Caching Strategy**: First request fetches data from GitHub API and caches it, subsequent requests return directly from database
- **Error Handling**: Proper exception handling and HTTP status code returns
- **Logging**: Uses SLF4J for logging
- **Transaction Management**: Uses Spring's `@Transactional` annotation to manage database transactions
- **Lombok**: Uses Lombok annotations to reduce boilerplate code (getters, setters, constructors)

## Notes

- GitHub API has rate limits (unauthenticated users: 60 requests/hour)
- H2 is an in-memory database, data will be lost after application restart
- Production environments should use persistent databases (e.g., PostgreSQL, MySQL)

## License

MIT License

