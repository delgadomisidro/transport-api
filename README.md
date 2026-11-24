# Transport Management API

REST API for managing transport orders, drivers, assignments and file attachments.

## Technologies

- Java 17 and Spring Boot
- Spring Data JPA and PostgreSQL
- Spring Security and JWT
- Swagger / OpenAPI
- Docker and Docker Compose
- JUnit 5 and Mockito
- Structured JSON logging

## Getting started

1. Clone the repository.
2. Copy `.env.example` to `.env`.
3. Configure your environment variables:

```dotenv
DB_PASSWORD=your_database_password
JWT_SECRET=your_base64_secret
API_USERNAME=admin
API_PASSWORD=your_api_password
```

Generate a JWT secret with OpenSSL (`openssl rand -base64 32`) or PowerShell:

```powershell
$bytes = New-Object byte[] 32
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
[Convert]::ToBase64String($bytes)
$rng.Dispose()
```

Copy the generated secret into `.env`. Do not commit real credentials.

4. Start the application:

```bash
docker compose up -d --build
```

- API: http://localhost:8082
- Swagger: http://localhost:8082/swagger-ui.html
- PostgreSQL: localhost:5433 (database: `transport_db`, user: `postgres`)

## Authentication

Send a POST request to `/api/auth/login`:

```json
{
  "username": "admin",
  "password": "your_api_password"
}
```

Use the returned `accessToken` in subsequent requests:

```http
Authorization: Bearer <token>
```

In Swagger, click **Authorize** and paste the token without the Bearer prefix.

## Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/login` | Login |
| POST | `/api/orders` | Create order |
| GET | `/api/orders` | List and filter orders |
| GET | `/api/orders/{id}` | Get order |
| PUT | `/api/orders/{id}/status` | Update status |
| POST | `/api/drivers` | Create driver |
| GET | `/api/drivers` | List drivers |
| POST | `/api/orders/{orderId}/assignments` | Assign driver |
| GET | `/api/orders/{orderId}/assignments/{assignmentId}` | Get assignment |
| POST | `/api/orders/{orderId}/assignments/{assignmentId}/attachments` | Upload files |
| GET | `/api/orders/{orderId}/assignments/{assignmentId}/attachments/{type}` | Download file |

## Business rules

- Order transitions: CREATED → IN_TRANSIT → DELIVERED, or CREATED → CANCELLED.
- Only active drivers can be assigned to orders in CREATED status.
- Each order supports one assignment.
- Upload both files using multipart/form-data: `document` (PDF) and `image` (PNG/JPG/JPEG). Maximum 10 MB per file and 20 MB per request, including overhead.
- Download types: `document` and `image`.

## Architecture

Layered architecture using Controllers, Services, Repositories, Entities and DTOs. Includes global exception handling, dynamic filters, optimistic locking and JWT authentication.

## Persistence and logs

PostgreSQL data and uploaded files are stored in Docker named volumes. To restart without deleting data:

```bash
docker compose down
docker compose up -d
```

Do not use `docker compose down -v` if you want to preserve data.

View application logs:

```bash
docker compose logs -f api
```

## Tests

Existing tests cover order and driver services and optimistic locking. Configure `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `API_USERNAME` and `API_PASSWORD` in your shell and use a reachable test PostgreSQL database.

```powershell
.\mvnw.cmd clean test
```

JWT authentication was manually tested with Postman. Additional automated JWT and attachment tests are pending.
