# Expense Tracker REST API

Spring Boot expense tracking with JWT authentication and smart budget notifications.

## Quick Start

Run with Docker (recommended for demo):

1) Clone and enter the repo

```bash
git clone https://github.com/AngelStanchev33/JAVA_WEB_PROJECT_Expense-tracker.git
cd JAVA_WEB_PROJECT_Expense-tracker
```

2) Build the JAR (required before Docker)

- Windows PowerShell
```powershell
./gradlew.bat clean build
```

- Linux/macOS
```bash
./gradlew clean build
```

3) Start containers from the project root (where `docker-compose.yml` is)

```bash
docker compose up --build
```

Access: API at `http://localhost:8080` • Swagger at `/swagger-ui/index.html`

Notes:
- The app runs with `SPRING_PROFILES_ACTIVE=dev` (extra Spring Security DEBUG logs only).
- The DB connection auto-targets Docker DB (`DATABASE_HOST=db` via compose). Running outside Docker uses `localhost` by default.
- If you see an error about missing `build/libs/*.jar`, run the Gradle build step and re-run `docker compose up --build`.

Run locally without Docker (optional):

```powershell
# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE="dev"
$env:JWT_SIGNING_KEY="dev-secret-change-me"
$env:FOREX_API_KEY="dev-forex-key"
$env:MYSQL_USER="root"
$env:MYSQL_PASSWORD="12345"
./gradlew.bat bootRun
```

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

## Tech Stack

Spring Boot • JWT • MySQL • Docker • Event-Driven Architecture

Built for learning modern Spring Boot development.

