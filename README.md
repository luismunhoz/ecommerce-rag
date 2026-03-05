# eCommerce Platform

A full-stack eCommerce application built with Spring Boot 3 (backend), React 18 (frontend), PostgreSQL + pgvector (database), and RabbitMQ (product sync messaging). Includes AI-powered semantic search via LangChain4j and OpenAI embeddings.

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Prerequisites](#prerequisites)
- [Environment Variables](#environment-variables)
- [Running with Docker (Recommended)](#running-with-docker-recommended)
- [Service URLs](#service-urls)
- [Swagger UI](#swagger-ui)
- [RabbitMQ Dashboard](#rabbitmq-dashboard)
- [IntelliJ DataSource Configuration](#intellij-datasource-configuration)
- [Running Locally Without Docker](#running-locally-without-docker)
- [Authentication](#authentication)
- [API Reference](#api-reference)
- [Semantic Search](#semantic-search)
- [Product Sync Queue](#product-sync-queue)

---

## Architecture Overview

```
┌─────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│   Frontend      │    │    Backend       │    │ PostgreSQL+pgvec │
│   React 18      │───▶│  Spring Boot 3   │───▶│   Port 5432      │
│   Port 3000     │    │   Port 8080      │    └──────────────────┘
└─────────────────┘    │                  │    ┌──────────────────┐
                       │                  │◀───│    RabbitMQ      │
                       │                  │    │   Port 5672      │
                       │                  │    └──────────────────┘
                       │                  │    ┌──────────────────┐
                       │                  │───▶│   OpenAI API     │
                       └──────────────────┘    │ (embeddings)     │
                                               └──────────────────┘
```

The backend follows **hexagonal (ports & adapters) architecture**. Product management is driven by an external system publishing messages to a RabbitMQ queue. Semantic search uses OpenAI `text-embedding-3-small` stored in pgvector.

---

## Prerequisites

### For Docker (recommended)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) or Docker Engine + Compose plugin
- An **OpenAI API key** — required for semantic search

### For local development
- Java 17
- Node.js 20+ and npm
- PostgreSQL 16 with the [pgvector](https://github.com/pgvector/pgvector) extension
- RabbitMQ 3.13
- An **OpenAI API key**

---

## Environment Variables

| Variable            | Required | Default       | Description                                      |
|---------------------|----------|---------------|--------------------------------------------------|
| `OPENAI_API_KEY`    | **Yes**  | —             | OpenAI API key for semantic search embeddings    |
| `DB_HOST`           | No       | `localhost`   | PostgreSQL host                                  |
| `DB_PORT`           | No       | `5432`        | PostgreSQL port                                  |
| `DB_NAME`           | No       | `ecommerce`   | PostgreSQL database name                         |
| `DB_USERNAME`       | No       | `ecommerce`   | PostgreSQL username                              |
| `DB_PASSWORD`       | No       | `ecommerce`   | PostgreSQL password                              |
| `RABBITMQ_HOST`     | No       | `localhost`   | RabbitMQ host                                    |
| `RABBITMQ_PORT`     | No       | `5672`        | RabbitMQ AMQP port                               |
| `RABBITMQ_USERNAME` | No       | `guest`       | RabbitMQ username                                |
| `RABBITMQ_PASSWORD` | No       | `guest`       | RabbitMQ password                                |
| `JWT_SECRET`        | No       | *(base64 key)*| JWT signing secret                               |
| `JWT_EXPIRATION`    | No       | `86400000`    | JWT expiry in milliseconds (default 24 h)        |
| `SERVER_PORT`       | No       | `8080`        | Backend HTTP port                                |

---

## Running with Docker (Recommended)

### 1. Set your OpenAI API key

```bash
export OPENAI_API_KEY=sk-...
```

### 2. Start all services

```bash
docker compose up --build
```

This starts four containers in order: RabbitMQ → PostgreSQL → Backend → Frontend.

> The first build takes several minutes as Gradle downloads dependencies. Subsequent builds are faster due to Docker layer caching.

### Start in detached mode

```bash
docker compose up --build -d
```

### Stop all services

```bash
docker compose down
```

### Stop and remove all data (volumes)

```bash
docker compose down -v
```

### View logs

```bash
# All services
docker compose logs -f

# Backend only
docker compose logs -f backend

# RabbitMQ only
docker compose logs -f rabbitmq
```

### Rebuild a single service

```bash
docker compose up --build backend
```

---

## Service URLs

| Service              | URL                                        | Notes                           |
|----------------------|--------------------------------------------|---------------------------------|
| Frontend             | http://localhost:3000                      | React app served via Nginx      |
| Backend API          | http://localhost:8080/api                  | Spring Boot REST API            |
| **Swagger UI**       | **http://localhost:8080/swagger-ui.html**  | Interactive API documentation   |
| OpenAPI JSON         | http://localhost:8080/v3/api-docs          | Raw OpenAPI 3.0 spec            |
| Health Check         | http://localhost:8080/actuator/health      | Spring Actuator endpoint        |
| RabbitMQ Dashboard   | http://localhost:15672                     | Management UI (guest / guest)   |
| PostgreSQL           | localhost:5432                             | Direct DB access                |

---

## Swagger UI

**URL:** http://localhost:8080/swagger-ui.html

The Swagger UI provides interactive documentation for every API endpoint. You can execute requests directly from the browser.

### Authenticating in Swagger UI

1. Call `POST /api/auth/login` in Swagger (or via curl) to obtain a JWT token.
2. Click the **Authorize** button (top right of the Swagger page).
3. Enter `Bearer <your-token>` in the **bearerAuth** field.
4. Click **Authorize** → **Close**.

All subsequent requests from Swagger will include the token automatically.

---

## RabbitMQ Dashboard

**URL:** http://localhost:15672

| Field    | Value   |
|----------|---------|
| Username | `guest` |
| Password | `guest` |

### Queue and Exchange details

| Resource    | Name                  | Type   |
|-------------|-----------------------|--------|
| Queue       | `product.sync.queue`  | Durable|
| Exchange    | `product.exchange`    | Direct |
| Routing Key | `product.sync`        |        |

### Publishing a test message from the dashboard

1. Go to **Exchanges** → click `product.exchange`
2. Under **Publish message**, set:
   - **Routing key:** `product.sync`
   - **Properties:** `content_type = application/json`
   - **Payload:** (see [Product Sync Queue](#product-sync-queue) section)
3. Click **Publish message**

---

## IntelliJ DataSource Configuration

Go to **View → Tool Windows → Database → + → Data Source → PostgreSQL**

| Field    | Value                                        |
|----------|----------------------------------------------|
| Host     | `localhost`                                  |
| Port     | `5432`                                       |
| Database | `ecommerce`                                  |
| Username | `ecommerce`                                  |
| Password | `ecommerce`                                  |
| URL      | `jdbc:postgresql://localhost:5432/ecommerce` |
| Driver   | PostgreSQL (IntelliJ will prompt to download)|

Click **Test Connection** to verify, then **OK**.

### Key tables

| Table                 | Description                                          |
|-----------------------|------------------------------------------------------|
| `users`               | Registered users and roles                           |
| `products`            | Product catalog                                      |
| `categories`          | Hierarchical product categories                      |
| `orders`              | Customer orders                                      |
| `order_items`         | Line items per order                                 |
| `shopping_carts`      | User shopping carts                                  |
| `cart_items`          | Items in each cart                                   |
| `product_embeddings`  | pgvector embeddings for semantic search              |

---

## Running Locally Without Docker

### Option A — Infra in Docker, app local (recommended for dev)

Start only the infrastructure services:

```bash
docker compose up -d postgres rabbitmq
```

Then run the backend and/or frontend locally.

### Backend

**Prerequisites:** Java 17, Gradle wrapper included in the repo.

```bash
export OPENAI_API_KEY=sk-...
./gradlew bootRun
```

Override any setting via environment variables:

```bash
OPENAI_API_KEY=sk-... \
DB_HOST=localhost DB_PORT=5432 DB_NAME=ecommerce \
DB_USERNAME=ecommerce DB_PASSWORD=ecommerce \
RABBITMQ_HOST=localhost RABBITMQ_PORT=5672 \
./gradlew bootRun
```

Build a standalone JAR:

```bash
./gradlew bootJar
OPENAI_API_KEY=sk-... java -jar build/libs/ecommerce-1.0.0.jar
```

Run tests:

```bash
./gradlew test
```

### Frontend

**Prerequisites:** Node.js 20+, npm.

```bash
cd ecommerce-frontend
npm install
npm start
```

The frontend dev server starts on **http://localhost:3000** and proxies all `/api` requests to `http://localhost:8080` automatically (configured in `package.json`).

---

## Authentication

The API uses **JWT Bearer tokens** (24-hour expiry). Most write operations and all cart/order operations require authentication.

### 1. Register a user

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

Response includes a `token` field. Use it in subsequent requests:

```bash
curl http://localhost:8080/api/orders \
  -H "Authorization: Bearer <token>"
```

### Roles

| Role         | Access                                                                              |
|--------------|-------------------------------------------------------------------------------------|
| `ROLE_USER`  | Browse products, manage own cart and orders                                         |
| `ROLE_ADMIN` | All of the above + create/update/delete products, categories, and manage all orders |

> To grant admin access, update the `user_roles` table directly:
> ```sql
> INSERT INTO user_roles (user_id, role) VALUES (<user_id>, 'ROLE_ADMIN');
> ```

---

## API Reference

> The full interactive API reference is available at **http://localhost:8080/swagger-ui.html**.

### Auth — `/api/auth`

| Method | Endpoint              | Auth | Description                           |
|--------|-----------------------|------|---------------------------------------|
| POST   | `/api/auth/register`  | No   | Register new user, returns JWT token  |
| POST   | `/api/auth/login`     | No   | Login, returns JWT token              |
| POST   | `/api/auth/logout`    | Yes  | Logout (client-side token discard)    |

---

### Products — `/api/products`

| Method | Endpoint                              | Auth  | Description                                        |
|--------|---------------------------------------|-------|----------------------------------------------------|
| GET    | `/api/products`                       | No    | List all products (paginated, sorted by date)      |
| GET    | `/api/products/{id}`                  | No    | Get product by ID                                  |
| GET    | `/api/products/active`                | No    | List all active products                           |
| GET    | `/api/products/category/{categoryId}` | No    | Products by category (paginated)                   |
| GET    | `/api/products/search?q={query}`      | No    | Keyword search by name (paginated)                 |
| GET    | `/api/products/semantic-search?q={query}` | No | **AI semantic search** — see section below     |
| POST   | `/api/products`                       | ADMIN | Create product (also indexes for semantic search)  |
| PUT    | `/api/products/{id}`                  | ADMIN | Update product (re-indexes for semantic search)    |
| DELETE | `/api/products/{id}`                  | ADMIN | Delete product (removes from semantic search)      |
| POST   | `/api/products/reindex`               | ADMIN | Re-index all products for semantic search          |

**Create product body:**
```json
{
  "name": "Wireless Headphones",
  "description": "Premium noise-cancelling headphones",
  "price": 199.99,
  "stockQuantity": 50,
  "sku": "WH-1000XM5",
  "imageUrl": "https://example.com/image.jpg",
  "categoryId": 1
}
```

**Pagination query params:** `?page=0&size=20&sort=createdAt,desc`

---

### Categories — `/api/categories`

| Method | Endpoint                      | Auth  | Description                      |
|--------|-------------------------------|-------|----------------------------------|
| GET    | `/api/categories`             | No    | List all categories              |
| GET    | `/api/categories/root`        | No    | List root (top-level) categories |
| GET    | `/api/categories/{id}`        | No    | Get category by ID               |
| GET    | `/api/categories/slug/{slug}` | No    | Get category by slug             |
| POST   | `/api/categories`             | ADMIN | Create category                  |
| PUT    | `/api/categories/{id}`        | ADMIN | Update category                  |
| DELETE | `/api/categories/{id}`        | ADMIN | Delete category                  |

**Create category (query string params):**
```
POST /api/categories?name=Electronics&description=Electronic+devices&parentId=1
```

---

### Cart — `/api/cart`

All cart endpoints require authentication.

| Method | Endpoint                                  | Description              |
|--------|-------------------------------------------|--------------------------|
| GET    | `/api/cart`                               | Get current user's cart  |
| POST   | `/api/cart/items`                         | Add item to cart         |
| PUT    | `/api/cart/items/{productId}?quantity={n}`| Update item quantity     |
| DELETE | `/api/cart/items/{productId}`             | Remove item from cart    |
| DELETE | `/api/cart`                               | Clear cart               |

**Add to cart body:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

---

### Orders — `/api/orders`

All order endpoints require authentication.

| Method | Endpoint                            | Auth  | Description                           |
|--------|-------------------------------------|-------|---------------------------------------|
| POST   | `/api/orders`                       | Yes   | Create order from item list           |
| POST   | `/api/orders/from-cart`             | Yes   | Create order from current cart        |
| GET    | `/api/orders`                       | Yes   | Get current user's orders             |
| GET    | `/api/orders/paged`                 | Yes   | Get current user's orders (paginated) |
| GET    | `/api/orders/{id}`                  | Yes   | Get order by ID                       |
| GET    | `/api/orders/number/{orderNumber}`  | Yes   | Get order by order number             |
| GET    | `/api/orders/admin/all`             | ADMIN | List all orders (paginated)           |
| PATCH  | `/api/orders/{id}/confirm`          | ADMIN | Confirm order                         |
| PATCH  | `/api/orders/{id}/ship`             | ADMIN | Mark order as shipped                 |
| PATCH  | `/api/orders/{id}/deliver`          | ADMIN | Mark order as delivered               |
| PATCH  | `/api/orders/{id}/cancel`           | Yes   | Cancel order                          |

**Create order body:**
```json
{
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 3, "quantity": 1 }
  ],
  "shippingAddress": "123 Main St, Springfield, IL 62701",
  "billingAddress": "123 Main St, Springfield, IL 62701",
  "paymentMethod": "CREDIT_CARD"
}
```

**Create order from cart (query params):**
```
POST /api/orders/from-cart?shippingAddress=123+Main+St&paymentMethod=CREDIT_CARD
```

**Order status flow:** `PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED`

---

## Semantic Search

The platform uses **OpenAI `text-embedding-3-small`** and **pgvector** to power natural language product search.

### How it works

1. When a product is created or updated, its name, category, and description are embedded via the OpenAI API and stored in the `product_embeddings` table.
2. On search, the user's query is embedded in the same way and compared against stored embeddings using cosine similarity.
3. Only results with a similarity score above **0.65** are returned. Results are ordered by relevance.
4. Only **active products that exist in the database** are returned.

### Endpoint

```
GET /api/products/semantic-search?q={query}&limit={n}
```

| Parameter | Required | Default | Description                          |
|-----------|----------|---------|--------------------------------------|
| `q`       | Yes      | —       | Natural language search query        |
| `limit`   | No       | `10`    | Maximum number of results to return  |

### Example queries

```bash
# Home office products
curl "http://localhost:8080/api/products/semantic-search?q=I+need+products+for+my+home+office"

# Gift ideas
curl "http://localhost:8080/api/products/semantic-search?q=gifts+for+a+gamer"

# Beverages
curl "http://localhost:8080/api/products/semantic-search?q=something+to+keep+my+drinks+cold"
```

### Initial setup / re-indexing

Products are indexed automatically on create and update. After a bulk data import or first run with existing data, call the re-index endpoint:

```bash
curl -X POST http://localhost:8080/api/products/reindex \
  -H "Authorization: Bearer <admin-token>"
```

> This calls the OpenAI API once per product. For large catalogs, expect it to take a few seconds.

---

## Product Sync Queue

The application listens on `product.sync.queue` for product management events published by an external system. Received products are automatically indexed for semantic search.

### Connection details

| Setting      | Value                                       |
|--------------|---------------------------------------------|
| Host         | `localhost` (or `rabbitmq` inside Docker)   |
| Port         | `5672`                                      |
| Username     | `guest`                                     |
| Password     | `guest`                                     |
| Exchange     | `product.exchange`                          |
| Routing Key  | `product.sync`                              |
| Queue        | `product.sync.queue`                        |

### Message format

All messages must be JSON with `Content-Type: application/json`.

| Field           | Type    | Required for | Description                      |
|-----------------|---------|--------------|----------------------------------|
| `action`        | String  | All          | `CREATE`, `UPDATE`, or `DELETE`  |
| `sku`           | String  | All          | Unique product identifier        |
| `name`          | String  | CREATE       | Product name (max 255 chars)     |
| `description`   | String  | No           | Product description (max 2000)   |
| `price`         | Decimal | CREATE       | Price, e.g. `29.99`              |
| `stockQuantity` | Integer | CREATE       | Stock quantity (≥ 0)             |
| `imageUrl`      | String  | No           | URL of the product image         |
| `categoryId`    | Long    | No           | ID of the category               |
| `active`        | Boolean | No           | Defaults to `true` on CREATE     |

### Examples

**Create a product:**
```json
{
  "action": "CREATE",
  "sku": "PROD-001",
  "name": "Wireless Mouse",
  "description": "Ergonomic wireless mouse with USB receiver",
  "price": 39.99,
  "stockQuantity": 200,
  "imageUrl": "https://example.com/mouse.jpg",
  "categoryId": 1,
  "active": true
}
```

**Update a product:**
```json
{
  "action": "UPDATE",
  "sku": "PROD-001",
  "price": 34.99,
  "stockQuantity": 180,
  "active": true
}
```

**Delete a product:**
```json
{
  "action": "DELETE",
  "sku": "PROD-001"
}
```

### Consumer behaviour

| Scenario                      | Behaviour                                                 |
|-------------------------------|-----------------------------------------------------------|
| `CREATE` — SKU is new         | Product is created and indexed for semantic search        |
| `CREATE` — SKU already exists | Treated as UPDATE (upsert), embedding is refreshed        |
| `UPDATE` — SKU found          | Product fields are updated, embedding is refreshed        |
| `UPDATE` — SKU not found      | Warning logged, message discarded                         |
| `DELETE` — SKU found          | Product is deleted and removed from semantic search index |
| `DELETE` — SKU not found      | Warning logged, message discarded                         |
| Missing `action` or `sku`     | Warning logged, message discarded                         |
| Unknown `action`              | Warning logged, message discarded                         |

> Partial updates are supported: only non-null fields in the UPDATE message are changed.
