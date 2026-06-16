# TaskFlow API

A production-style REST API for project and task management — built with Spring Boot 3, Java 21, PostgreSQL, and JWT authentication. Containerized with Docker and deployed to AWS ECS Fargate with managed RDS PostgreSQL.

## Tech Stack

- **Language:** Java 21 (LTS)
- **Framework:** Spring Boot 3.4
- **Database:** PostgreSQL 16 + Flyway versioned migrations
- **Security:** Spring Security + JWT (HS512) + BCrypt password hashing
- **Validation:** Jakarta Bean Validation
- **API Docs:** Springdoc OpenAPI / Swagger UI
- **Build:** Maven
- **Containerization:** Docker + Docker Compose (multi-stage builds)
- **Deployment:** AWS ECS Fargate + RDS PostgreSQL + ECR

## Features

- User registration and login with JWT-based stateless authentication
- Per-user (row-level) authorization — users only access their own data
- Nested resource authorization — comments inherit permissions from parent project
- Cascading deletes at the database level via FK constraints
- Global exception handler returning structured JSON error responses
- Field-level validation errors for invalid request bodies
- Anti-enumeration on auth failures (same response for wrong email vs wrong password)
- Interactive API documentation at `/swagger-ui/index.html`

## Architecture

Strict layered separation:

```
HTTP Request
    ↓
Spring Security Filter Chain (JWT validation)
    ↓
Controller (HTTP, DTO validation, ResponseEntity)
    ↓
Service (business logic, transactions, authorization)
    ↓
Repository (Spring Data JPA)
    ↓
PostgreSQL
```

- Entities never leave the service layer — controllers only see DTOs
- Constructor injection with `final` fields throughout
- `@Transactional(readOnly = true)` at class level, overridden per write method

## Running Locally

```bash
docker compose up --build
```

App starts at <http://localhost:8080>
Swagger UI: <http://localhost:8080/swagger-ui/index.html>

## Running on AWS

The image is pushed to ECR and run on ECS Fargate. Configuration is externalized via environment variables — the same image runs locally and in production. Database connection is configured via:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`

## Key API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | Register a new user |
| POST | `/auth/login` | Authenticate, receive JWT |
| GET | `/api/projects` | List current user's projects |
| POST | `/api/projects` | Create a project |
| GET | `/api/projects/{id}` | Get a specific project |
| DELETE | `/api/projects/{id}` | Delete a project |
| POST | `/api/projects/{projectId}/tasks` | Create a task in a project |
| GET | `/api/projects/{projectId}/tasks` | List tasks |
| PATCH | `/api/tasks/{id}/status` | Update task status |
| POST | `/api/tasks/{taskId}/comments` | Add a comment |
| GET | `/api/tasks/{taskId}/comments` | List comments |

Full OpenAPI spec available at `/v3/api-docs`.

## Screenshots

<!-- Add screenshots in a /screenshots folder and reference them here -->
![Swagger UI](screenshots/swagger-ui.png)
![ECS Service Running](screenshots/ecs-service.png)
![Postman API Test](screenshots/postman-test.png)

## Lessons Learned

- **Multi-stage Docker builds** cut the final image size by ~60% by discarding the JDK after compilation
- **Bean cascade errors are misleading** — when ECS deploys failed during JPA initialization, the real cause was buried five levels deep in `Caused by:` chains. The lowest line, not the highest, is the truth
- **Externalized config via environment variables** is what lets the same image run locally, in tests, and in production unchanged

## Author

[Neehar Salimetla](https://linkedin.com/in/neehar-salimetla-9b8b4a197) — Software Engineer