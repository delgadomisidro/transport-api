## Running with Docker

### Requirements

- Docker Desktop
- Docker Compose

### Setup

1. Clone the repository.
2. Copy `.env.example` to `.env`.
3. Set a PostgreSQL password in `.env`.
4. Run:

```bash
docker compose up --build -d
```

### API

The application will be available at:

`http://localhost:8082`

### Example request

```http
POST http://localhost:8082/api/orders
Content-Type: application/json
```

```json
{
  "origin": "CDMX",
  "destination": "Puebla"
}
```

### Useful commands

```bash
docker compose ps
docker compose logs -f api
docker compose down
```

PostgreSQL data is persisted using a Docker volume.

**Note:** The `.env` file must not be committed to Git. The Docker configuration is intended for local evaluation; the development security rules must be replaced with JWT authentication before production use.