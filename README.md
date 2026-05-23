# PMST Auth Gateway

Local auth gateway that replicates AWS Cognito behavior for development.

## Purpose

This gateway provides production-like authentication for local development without requiring AWS Cognito. It issues JWTs with the same structure as Cognito, enabling zero code changes when deploying to production.

## Architecture

```
Frontend (localhost:4200) → Gateway (localhost:9000) → Backend (localhost:8080) → PostgreSQL
```

- **Gateway handles**: `/auth/**` endpoints (login, register, refresh, me)
- **Gateway proxies**: `/api/**` requests to backend
- **Backend validates**: JWTs using same logic as production (Cognito-compatible claims)

## Features

- JWT generation with Cognito-compatible claims structure
- BCrypt password hashing
- Refresh token support
- CORS configuration for Angular frontend
- Proxy all API requests to backend

## Local Development

### Prerequisites

- Java 21
- Maven 3.8+
- PostgreSQL (running via docker-compose in pmst-api-service)

### Setup

1. Start PostgreSQL:
```bash
cd D:\pmst-services\pmst-api-service
docker compose up -d
```

2. Start backend:
```bash
cd D:\pmst-services\pmst-api-service
mvn spring-boot:run
```

3. Start gateway:
```bash
cd D:\pmst-services\pmst-auth-gateway
mvn spring-boot:run
```

4. Start frontend:
```bash
cd D:\pmstmigrate
ng serve
```

### Configuration

Gateway runs on port 9000. Configuration in `src/main/resources/application.yml`:

```yaml
server:
  port: 9000

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pmst
    username: pmst
    password: pmst

gateway:
  backend:
    url: http://localhost:8080

jwt:
  secret: pmst-local-dev-secret-key-change-in-production
  issuer: http://localhost:9000
  audience: pmst-local-client
  expiration: 3600000 # 1 hour
```

## JWT Structure

Gateway issues JWTs with Cognito-compatible claims:

```json
{
  "sub": "user-uuid",
  "email": "user@example.com",
  "email_verified": true,
  "iss": "http://localhost:9000",
  "aud": "pmst-local-client",
  "token_use": "access",
  "auth_time": 1234567890,
  "exp": 1234571490,
  "iat": 1234567890,
  "cognito:groups": ["user"],
  "username": "johndoe"
}
```

## API Endpoints

### Auth (handled by gateway)

- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `POST /auth/refresh` - Refresh access token
- `GET /auth/me` - Get current user (requires JWT)

### Proxy (forwarded to backend)

- All `/api/**` requests are proxied to `http://localhost:8080`

## Production Deployment

In production, this gateway is **not used**. The architecture is:

```
Frontend → API Gateway → Lambda (Backend) → RDS PostgreSQL
Frontend → Cognito User Pool (auth)
```

To deploy to production:
1. Change frontend `apiUrl` to production API Gateway URL
2. No code changes needed in backend (JWT validation works the same)
3. Remove gateway from infrastructure

## Database

Gateway uses the same PostgreSQL database as backend. The `gateway_users` table is created automatically:

```sql
CREATE TABLE gateway_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    username VARCHAR(100) UNIQUE NOT NULL,
    display_name VARCHAR(255),
    role VARCHAR(50) DEFAULT 'user',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
```

## Security Notes

- **Development only**: This gateway is for local development only
- **Secret key**: Change `jwt.secret` in production
- **Password hashing**: Uses BCrypt with default strength
- **JWT signing**: HMAC SHA256 (same as Cognito for compatibility)
