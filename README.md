# n11_patika_final

Microservice-based e-commerce project with a Next.js frontend and Spring Boot backend services.

## Project Structure

- `frontend`: Next.js 14 storefront and admin UI
- `backend/api-gateway`: single entry point for backend APIs
- `backend/discovery-server`: Eureka service discovery
- `backend/config-server`: central configuration server
- `backend/product-service`: product management and stock operations
- `backend/cart-service`: cart operations
- `backend/order-service`: order creation and order status flow
- `backend/payment-service`: payment flow with Iyzico integration
- `config-repo`: externalized service configuration files
- `deploy`: local bootstrap files for Postgres and Keycloak
- `infrastructure`: infrastructure-related files

## Architecture

The system is split into small services with clear responsibilities.

- `frontend` talks to the system through `api-gateway`
- `config-server` provides service configuration
- `discovery-server` keeps service registration and discovery
- each business service owns its own database
- `RabbitMQ` is used for event-based communication in the order and payment flow
- `Keycloak` handles authentication and role-based access

## Main Flow

1. User signs in with Keycloak.
2. User browses products and adds items to the cart.
3. Order service starts checkout.
4. Product, order, cart, and payment services communicate through events.
5. Payment service handles Iyzico checkout callback and updates order state.

## Tech Stack

- Frontend: Next.js, React, TypeScript, Tailwind CSS, NextAuth
- Backend: Java, Spring Boot, Spring Cloud
- Data: PostgreSQL
- Messaging: RabbitMQ
- Auth: Keycloak
- Containers: Docker Compose

## Local Run

1. Copy `.env.compose.example` to `.env` and update values if needed.
2. Start the full stack:

```bash
docker compose up --build
```

3. Optional product seeding:

```bash
docker compose --profile seed up product-seeder
```

## Default Local Ports

- `3000`: frontend
- `8079`: api gateway
- `8081`: product service
- `8082`: cart service
- `8083`: order service
- `8084`: payment service
- `8085`: Keycloak
- `8761`: discovery server
- `8888`: config server
- `5432`: PostgreSQL
- `5672`: RabbitMQ
- `15672`: RabbitMQ management

## Design Notes

- services are separated by business domain
- synchronous calls are used where direct data access is needed
- asynchronous messaging is used for checkout and payment steps
- configuration is centralized to keep service setup consistent
- authentication is handled outside business services

## Testing

- frontend unit tests: `npm test` in `frontend`
- frontend e2e tests: `npm run test:e2e` in `frontend`
- backend tests: run `./mvnw test` inside each backend service

## Note

GitHub workflow files are not included in this README because that part is still under development.
