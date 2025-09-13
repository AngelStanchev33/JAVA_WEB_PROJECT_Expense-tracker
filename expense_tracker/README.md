# Expense Tracker REST API

Spring Boot expense tracking with JWT authentication and smart budget notifications.

## Quick Start

Option A — Local (dev):

```powershell
# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE="dev"
$env:JWT_SIGNING_KEY="dev-secret-change-me"
$env:FOREX_API_KEY="dev-forex-key"
$env:MYSQL_USER="root"
$env:MYSQL_PASSWORD="12345"
./gradlew.bat bootRun
```

Option B — Docker Compose:

```bash
./gradlew clean build
docker compose up --build
```

API: `http://localhost:8080` • Swagger: `/swagger-ui/index.html`

## Features

- **JWT Authentication** with owner-only data access
- **Smart Budget Alerts** - Notifications at 75%, 50%, 25%, 0% remaining
- **Currency Support** - EUR and BGN with automatic conversion
- **Email Integration** - Registration emails work in testing
- **Custom Security** - SpEL expressions for method-level protection

## API Endpoints

```bash
POST /auth/register     # Register user
POST /auth/login        # Get JWT token

GET  /expenses/my       # Your expenses only
POST /expenses/create   # Create expense (triggers budget check)
PUT  /expenses/{id}     # Update (owner only)
DELETE /expenses/{id}   # Delete (owner only)

GET  /budgets/my        # Your budgets
POST /budgets/create    # Create budget

GET  /notifications/my  # Budget alerts
```

## Example Usage

```bash
# Register
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password123","firstName":"Test","lastName":"User"}'

# Login
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password123"}'

# Create budget in EUR, add expense in BGN - auto-converts!
```

## Tech Stack

Spring Boot • JWT • MySQL • Docker • Event-Driven Architecture

Built for learning modern Spring Boot development.

## Environment Variables

Required (no defaults committed):

- `JWT_SIGNING_KEY` — HMAC secret for JWT
- `FOREX_API_KEY` — key for OpenExchangeRates
- `MYSQL_USER`, `MYSQL_PASSWORD` — DB credentials

Optional/dev:

- `SPRING_PROFILES_ACTIVE=dev` — enables DEBUG and SQL logging (via env)
- `DATABASE_HOST` — defaults to `localhost`; set to `db` in Docker

Tip: create a local `.env` for your shell. Do not commit secrets. See `.env.example`.
