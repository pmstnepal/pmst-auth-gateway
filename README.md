# PMST Auth Gateway

> **Status: Future Scope** — Not active. Local development uses `cognito-local` Docker container for auth.

## Purpose

This service is planned as a **Spring Boot API Gateway** for routing requests across PMST microservices once multiple services are live. It is **not** used for local authentication — that is handled by `jagregory/cognito-local` Docker container in `pmst-api-service/docker-compose.yml`.

## Planned Architecture (Future)

When multiple services are deployed, this gateway will serve as the single entry point:

```
Frontend → pmst-auth-gateway (port 9000)
               ↓ routes by path
               ├── /api/**       → pmst-api-service (port 8080)
               ├── /tickets/**   → pmst-ticketing-service (port 8081)
               └── /auth/**      → handled locally (JWT passthrough in prod)
```

## Current Local Development Architecture

**This gateway is NOT part of the local dev stack.** Use this instead:

```
Frontend (localhost:4200)
    ↓ all API calls
pmst-api-service (localhost:8080)
    ↓ auth via
cognito-local Docker (localhost:9229)
    ↓ data
PostgreSQL Docker (localhost:5432)
```

See `pmst-api-service` for local dev setup instructions.

## Production Architecture

In production, AWS API Gateway handles routing — this service is not deployed to prod.

```
Frontend → AWS API Gateway → Lambda (pmst-api-service) → RDS PostgreSQL
Frontend → AWS Cognito User Pool (auth)
```

## Technology Stack

- Java 21
- Spring Boot 3.2.5
- Spring Security (OAuth2 Resource Server)
- Spring Web (servlet-based, not reactive)
- PostgreSQL (via Spring Data JPA)
- JJWT 0.12.3 (JWT generation/validation)

## When Will This Be Activated?

Once `pmst-ticketing-service` is live and both services need to be accessed from the same frontend origin, this gateway will be activated to:

1. Route `/api/**` to `pmst-api-service`
2. Route `/tickets/**` to `pmst-ticketing-service`
3. Handle JWT passthrough (validate + forward to backend services)
4. Centralise CORS configuration

## Related Repos

| Repo | Purpose | Port |
|------|---------|------|
| `pmst-api-service` | Articles, Galleries, Users, Comments | 8080 |
| `pmst-ticketing-service` | Events, Tickets, RSVP (planned) | 8081 |
| `pmst-angular-ui` | Angular 17 frontend | 4200 |
