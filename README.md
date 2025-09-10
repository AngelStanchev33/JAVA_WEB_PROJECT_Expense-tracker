# Expense Tracker REST API

Spring Boot expense tracking with JWT authentication and smart budget notifications.

## Quick Start

```bash
git clone https://github.com/AngelStanchev33/JAVA_WEB_PROJECT_Expense-tracker.git
cd JAVA_WEB_PROJECT_Expense-tracker
docker compose up
```

API: `http://localhost:8080`

**Useful Links:**
- [Swagger UI](http://localhost:8080/swagger-ui/index.html) - API Documentation
- [Google OAuth2](http://localhost:8080/oauth2/authorization/google) - Google Login

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
