# BancaRojo — Banking REST API

Backend REST API for a digital banking system built with **Java 21** and **Spring Boot 3.5**, containerized with **Docker** and backed by **PostgreSQL** and **Redis**.

## Modules & Endpoints

Located in `backend/src/main/java/bancarojo_backend/controller`:

- `AuthController` — Authentication & JWT token issuance
- `UserController` — User profile management
- `AccountController` — Bank account operations
- `TransactionController` — Transfers and transaction history

## Tech Stack

- **Java 21** + **Spring Boot 3.5.13**
- **Spring Web**, **Spring Data JPA**, **Spring Security**, **Bean Validation**
- **PostgreSQL 16** + **Redis 7** (`backend/compose.yaml`)
- **Docker** (`backend/Dockerfile`)

## Local Setup

```bash
cd backend
cp .env.example .env
docker compose up -d
./mvnw spring-boot:run
```
