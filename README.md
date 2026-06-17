# Reward Management API

A Spring Boot REST API that calculates reward points for customers based on their transaction history.

## How it works

Customers earn points based on the amount spent per transaction:
- Over $100 → 2 points for every dollar above $100, plus 1 point for every dollar between $50–$100
- Between $50–$100 → 1 point for every dollar above $50
- $50 or below → no points

Example: a $120 transaction earns `2×20 + 1×50 = 90 points`

## Running the app

```
mvn spring-boot:run
```

H2 console is available at `http://localhost:8080/h2-console`  
Swagger UI is available at `http://localhost:8080/swagger-ui.html`

The database is seeded with sample customers and transactions on startup via `data.sql`.

## API Endpoints

**Customers**
```
POST   /api/v1/customers           create a customer
GET    /api/v1/customers           list all customers
GET    /api/v1/customers/{id}      get customer by id
PUT    /api/v1/customers/{id}      update customer
DELETE /api/v1/customers/{id}      delete customer (also deletes their transactions)
```

**Transactions**
```
POST   /api/v1/transactions                        add a transaction
GET    /api/v1/transactions/customer/{customerId}  get all transactions for a customer
```

**Rewards**
```
GET    /api/v1/rewards                  reward summary for all customers (last 3 months)
GET    /api/v1/rewards/{customerId}     reward summary for a specific customer
GET    /api/v1/rewards/{customerId}?startDate=2026-04-01&endDate=2026-06-30   filter by date range
```

## Tech stack

- Java 17, Spring Boot 4.1.0
- Spring Data JPA, H2 (in-memory)
- Bean Validation, springdoc-openapi
- JUnit 5, Mockito, MockMvc
