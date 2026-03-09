# eCommerce Platform

A full-stack eCommerce application built with Spring Boot 3 (backend), React 18 (frontend),
PostgreSQL + pgvector (database), and RabbitMQ (product sync messaging).

Includes a full **Retrieval-Augmented Generation (RAG)** pipeline powered by LangChain4j and
OpenAI: semantic product search via vector embeddings, and an AI chat assistant that generates
grounded product recommendations from your catalog.

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
  - [Auth](#auth--apiauth)
  - [Products](#products--apiproducts)
  - [Categories](#categories--apicategories)
  - [Cart](#cart--apicart)
  - [Orders](#orders--apiorders)
  - [AI Chat (RAG)](#ai-chat-rag--apichat)
- [RAG Pipeline](#rag-pipeline)
  - [Semantic Search](#semantic-search)
  - [AI Chat Assistant](#ai-chat-assistant)
  - [Initial Indexing](#initial-indexing--reindexing)
- [Product Sync Queue](#product-sync-queue)

---

## Architecture Overview

```
┌─────────────────┐    ┌──────────────────────────────┐    ┌──────────────────┐
│   Frontend      │    │          Backend             │    │ PostgreSQL 16    │
│   React 18      │───▶│      Spring Boot 3.2         │───▶│ + pgvector       │
│   Port 3000     │    │      Port 8080               │    │   Port 5432      │
└─────────────────┘    │                              │    └──────────────────┘
                       │  ┌────────────────────────┐  │    ┌──────────────────┐
                       │  │ Hexagonal Architecture │  │◀───│    RabbitMQ      │
                       │  │  Ports & Adapters      │  │    │   Port 5672      │
                       │  └────────────────────────┘  │    └──────────────────┘
                       │                              │    ┌──────────────────┐
                       │                              │───▶│   OpenAI API     │
                       └──────────────────────────────┘    │ • text-embedding │
                                                           │   -3-small       │
                                                           │ • gpt-4o-mini    │
                                                           └──────────────────┘
```

### RAG pipeline flow

```
WRITE TIME (indexing)                   READ TIME (chat)
─────────────────────                   ────────────────
Product created / updated               POST /api/chat/ask
         │                                      │
         ▼                                      ▼
  name + category + description        Embed question
         │                             → query pgvector
         ▼                             → top-5 products
  text-embedding-3-small                        │
  → float[1536]                                 ▼
         │                             Build prompt with
         ▼                             products as context
  pgvector product_embeddings                   │
  (HNSW cosine index)                           ▼
                                       gpt-4o-mini
                                       → grounded answer
                                       + source products
```

The backend follows **hexagonal (ports & adapters) architecture**. Product management is also
driven by an external system publishing messages to a RabbitMQ queue. All product writes
(REST or RabbitMQ) automatically keep the vector index in sync.

---

## Prerequisites

### For Docker (recommended)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) or Docker Engine + Compose plugin
- An **OpenAI API key** : required for semantic search and the AI chat assistant

### For local development
- Java 17
- Node.js 20+ and npm
- PostgreSQL 16 with the [pgvector](https://github.com/pgvector/pgvector) extension
- RabbitMQ 3.13
- An **OpenAI API key**

---

## Environment Variables

| Variable            | Required | Default        | Description                                   |
|---------------------|----------|----------------|-----------------------------------------------|
| `OPENAI_API_KEY`    | **Yes**  | -              | OpenAI API key : used for embeddings and chat |
| `DB_HOST`           | No       | `localhost`    | PostgreSQL host                               |
| `DB_PORT`           | No       | `5432`         | PostgreSQL port                               |
| `DB_NAME`           | No       | `ecommerce`    | PostgreSQL database name                      |
| `DB_USERNAME`       | No       | `ecommerce`    | PostgreSQL username                           |
| `DB_PASSWORD`       | No       | `ecommerce`    | PostgreSQL password                           |
| `RABBITMQ_HOST`     | No       | `localhost`    | RabbitMQ host                                 |
| `RABBITMQ_PORT`     | No       | `5672`         | RabbitMQ AMQP port                            |
| `RABBITMQ_USERNAME` | No       | `guest`        | RabbitMQ username                             |
| `RABBITMQ_PASSWORD` | No       | `guest`        | RabbitMQ password                             |
| `JWT_SECRET`        | No       | *(base64 key)* | JWT signing secret                            |
| `JWT_EXPIRATION`    | No       | `86400000`     | JWT expiry in milliseconds (default 24 h)     |
| `SERVER_PORT`       | No       | `8080`         | Backend HTTP port                             |

### RAG configuration (application.yml)

These are set in `src/main/resources/application.yml` and can be overridden at runtime:

| Property                          | Default       | Description                                           |
|-----------------------------------|---------------|-------------------------------------------------------|
| `app.semantic-search.max-results` | `10`          | Max products returned by `/semantic-search`           |
| `app.semantic-search.min-score`   | `0.65`        | Cosine similarity threshold (0–1); lower = more noise |
| `app.rag.chat-model`              | `gpt-4o-mini` | OpenAI model used for answer generation               |
| `app.rag.temperature`             | `0.3`         | LLM temperature (0 = deterministic, 1 = creative)     |
| `app.rag.max-context-products`    | `5`           | Number of retrieved products injected into the prompt |

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
```

### Rebuild a single service

```bash
docker compose up --build backend
```

---

## Service URLs

| Service              | URL                                        | Notes                           |
|----------------------|--------------------------------------------|---------------------------------|
| Frontend             | http://localhost:3000                      | React app                       |
| Backend API          | http://localhost:8080/api                  | Spring Boot REST API            |
| **Swagger UI**       | **http://localhost:8080/swagger-ui.html**  | Interactive API documentation   |
| OpenAPI JSON         | http://localhost:8080/v3/api-docs          | Raw OpenAPI 3.0 spec            |
| Health Check         | http://localhost:8080/actuator/health      | Spring Actuator endpoint        |
| RabbitMQ Dashboard   | http://localhost:15672                     | Management UI (guest / guest)   |
| PostgreSQL           | localhost:5432                             | Direct DB access                |

---

## Swagger UI

**URL:** http://localhost:8080/swagger-ui.html

The Swagger UI provides interactive documentation for every API endpoint.

### Authenticating in Swagger UI

1. Call `POST /api/auth/login` to obtain a JWT token.
2. Click the **Authorize** button (top right).
3. Enter `Bearer <your-token>` in the **bearerAuth** field.
4. Click **Authorize** → **Close**.

---

## RabbitMQ Dashboard

**URL:** http://localhost:15672 , credentials: `guest` / `guest`

| Resource    | Name                  | Type    |
|-------------|-----------------------|---------|
| Queue       | `product.sync.queue`  | Durable |
| Exchange    | `product.exchange`    | Direct  |
| Routing Key | `product.sync`        |         |

### Publishing a test message

1. Go to **Exchanges** → click `product.exchange`
2. Set **Routing key:** `product.sync`, **Properties:** `content_type = application/json`
3. Paste a payload from the [Product Sync Queue](#product-sync-queue) section and click **Publish message**

---

## IntelliJ DataSource Configuration

**View → Tool Windows → Database → Data Source → PostgreSQL**

| Field    | Value                                        |
|----------|----------------------------------------------|
| Host     | `localhost`                                  |
| Port     | `5432`                                       |
| Database | `ecommerce`                                  |
| Username | `ecommerce`                                  |
| Password | `ecommerce`                                  |
| URL      | `jdbc:postgresql://localhost:5432/ecommerce` |

### Key tables

| Table                | Description                                             |
|----------------------|---------------------------------------------------------|
| `users`              | Registered users and roles                              |
| `categories`         | Hierarchical product categories                         |
| `products`           | Product catalog                                         |
| `orders`             | Customer orders                                         |
| `order_items`        | Line items per order                                    |
| `shopping_carts`     | User shopping carts                                     |
| `cart_items`         | Items in each cart                                      |
| `product_embeddings` | pgvector embeddings (vector(1536), HNSW index)          |

---

## Running Locally Without Docker

### Option A: Infra in Docker, app local (recommended for dev)

```bash
docker compose up -d postgres rabbitmq
```

### Backend

```bash
export OPENAI_API_KEY=sk-...
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

```bash
cd ecommerce-frontend
npm install
npm start   # http://localhost:3000 , proxies /api to :8080
```

---

## Authentication

The API uses **JWT Bearer tokens** (24-hour expiry).

### Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123","firstName":"John","lastName":"Doe"}'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
# → { "token": "eyJ...", "username": "...", "roles": [...] }
```

Use the token in subsequent requests:

```bash
curl http://localhost:8080/api/orders \
  -H "Authorization: Bearer <token>"
```

### Roles

| Role         | Access                                                                              |
|--------------|-------------------------------------------------------------------------------------|
| `ROLE_USER`  | Browse products, manage own cart and orders                                         |
| `ROLE_ADMIN` | All of the above + create/update/delete products, categories, and manage all orders |

> Grant admin access:
> ```sql
> INSERT INTO user_roles (user_id, role) VALUES (<user_id>, 'ROLE_ADMIN');
> ```

---

## API Reference

> Full interactive reference: **http://localhost:8080/swagger-ui.html**

### Auth : `/api/auth`

| Method | Endpoint             | Auth | Description                          |
|--------|----------------------|------|--------------------------------------|
| POST   | `/api/auth/register` | No   | Register new user, returns JWT token |
| POST   | `/api/auth/login`    | No   | Login, returns JWT token             |
| POST   | `/api/auth/logout`   | Yes  | Logout (client-side token discard)   |

---

### Products : `/api/products`

| Method | Endpoint                                  | Auth  | Description                             |
|--------|-------------------------------------------|-------|-----------------------------------------|
| GET    | `/api/products`                           | No    | List all products (paginated)           |
| GET    | `/api/products/{id}`                      | No    | Get product by ID                       |
| GET    | `/api/products/active`                    | No    | List all active products                |
| GET    | `/api/products/category/{categoryId}`     | No    | Products by category (paginated)        |
| GET    | `/api/products/search?q={query}`          | No    | Keyword search by name (paginated)      |
| GET    | `/api/products/semantic-search?q={query}` | No    | Semantic search, returns ranked list    |
| POST   | `/api/products`                           | ADMIN | Create product (auto-indexes embedding) |
| PUT    | `/api/products/{id}`                      | ADMIN | Update product (re-indexes embedding)   |
| DELETE | `/api/products/{id}`                      | ADMIN | Delete product (removes from index)     |
| POST   | `/api/products/reindex`                   | ADMIN | Reindex all products                    |

**Create / update product body:**
```json
{
  "name": "Wireless Headphones",
  "description": "Premium noise-cancelling headphones",
  "price": 199.99,
  "stockQuantity": 50,
  "sku": "WH-001",
  "imageUrl": "https://example.com/image.jpg",
  "categoryId": 1
}
```

**Pagination:** `?page=0&size=20&sort=createdAt,desc`

---

### Categories : `/api/categories`

| Method | Endpoint                      | Auth  | Description                      |
|--------|-------------------------------|-------|----------------------------------|
| GET    | `/api/categories`             | No    | List all categories              |
| GET    | `/api/categories/root`        | No    | List root (top level) categories |
| GET    | `/api/categories/{id}`        | No    | Get category by ID               |
| GET    | `/api/categories/slug/{slug}` | No    | Get category by slug             |
| POST   | `/api/categories`             | ADMIN | Create category                  |
| PUT    | `/api/categories/{id}`        | ADMIN | Update category                  |
| DELETE | `/api/categories/{id}`        | ADMIN | Delete category                  |

---

### Cart : `/api/cart`

All cart endpoints require authentication.

| Method | Endpoint                                   | Description              |
|--------|--------------------------------------------|--------------------------|
| GET    | `/api/cart`                                | Get current user's cart  |
| POST   | `/api/cart/items`                          | Add item to cart         |
| PUT    | `/api/cart/items/{productId}?quantity={n}` | Update item quantity     |
| DELETE | `/api/cart/items/{productId}`              | Remove item from cart    |
| DELETE | `/api/cart`                                | Clear cart               |

**Add to cart body:**
```json
{ "productId": 1, "quantity": 2 }
```

---

### Orders : `/api/orders`

All order endpoints require authentication.

| Method | Endpoint                           | Auth  | Description                           |
|--------|------------------------------------|-------|---------------------------------------|
| POST   | `/api/orders`                      | Yes   | Create order from item list           |
| POST   | `/api/orders/from-cart`            | Yes   | Checkout cart → order                 |
| GET    | `/api/orders`                      | Yes   | Get current user's orders             |
| GET    | `/api/orders/paged`                | Yes   | Get current user's orders (paginated) |
| GET    | `/api/orders/{id}`                 | Yes   | Get order by ID                       |
| GET    | `/api/orders/number/{orderNumber}` | Yes   | Get order by order number             |
| GET    | `/api/orders/admin/all`            | ADMIN | List all orders (paginated)           |
| PATCH  | `/api/orders/{id}/confirm`         | ADMIN | PENDING → CONFIRMED                   |
| PATCH  | `/api/orders/{id}/ship`            | ADMIN | CONFIRMED → SHIPPED                   |
| PATCH  | `/api/orders/{id}/deliver`         | ADMIN | SHIPPED → DELIVERED                   |
| PATCH  | `/api/orders/{id}/cancel`          | Yes   | Cancel order                          |

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

**Checkout from cart:**
```
POST /api/orders/from-cart?shippingAddress=123+Main+St&paymentMethod=CREDIT_CARD
```

**Order status flow:** `PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED`

---

### AI Chat (RAG) : `/api/chat`

No authentication required.

| Method | Endpoint        | Auth | Description                              |
|--------|-----------------|------|------------------------------------------|
| POST   | `/api/chat/ask` | No   | Ask the AI assistant , full RAG pipeline |

**Request:**
```json
{ "question": "What laptop should I buy for video editing under $2000?" }
```

**Response:**
```json
{
  "answer": "For video editing under $2000, I'd recommend the Dell XPS 15 9530 at $1,849.99...",
  "sources": [
    { "id": 1, "name": "Dell XPS 15 9530", "price": 1849.99, "inStock": true, ... },
    { "id": 2, "name": "Apple MacBook Pro 14\" M3", "price": 1999.99, "inStock": true, ... }
  ]
}
```

The `sources` array contains the products retrieved from pgvector that were injected as context
into the LLM prompt. Use it to display transparent citations in your UI.

---

## RAG Pipeline

### Semantic Search

`GET /api/products/semantic-search?q={query}&limit={n}` , **retrieval only**.

Returns a ranked list of products most semantically similar to the query.
No AI generated text, just the products ordered by cosine similarity score.

| Parameter | Required | Default | Description                         |
|-----------|----------|---------|-------------------------------------|
| `q`       | Yes      | —       | Natural language query              |
| `limit`   | No       | `10`    | Maximum number of results           |

```bash
curl "http://localhost:8080/api/products/semantic-search?q=wireless+mouse+for+gaming&limit=5"
```

**How it works:**
1. The query is embedded with `text-embedding-3-small` (same model used at index time).
2. pgvector finds the top-N stored embeddings with cosine similarity ≥ 0.65.
3. Product IDs are decoded from the embedding UUIDs; only active products are returned.
4. Results are ordered by relevance score descending.

---

### AI Chat Assistant

`POST /api/chat/ask` : **full RAG pipeline** (retrieval + augmented generation).

Retrieves relevant products from pgvector, injects them as structured context into a prompt,
and asks `gpt-4o-mini` to generate a grounded, human-readable recommendation.

```bash
curl -X POST http://localhost:8080/api/chat/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "I need a complete home office setup with a sit stand desk"}'
```

**Example questions:**
- `"What laptop should I buy for video editing under $2000?"`
- `"I need a complete home office setup with a sit stand desk"`
- `"What is the best wireless gaming mouse you have?"`
- `"Something lightweight for work trips"`

**Frontend:** In the React app, toggle **AI Search** in the search bar to use the chat endpoint.
The AI generated answer is displayed above the product results with a purple **AI** badge,
followed by the source products used as context.

---

### Initial Indexing / Reindexing

Products are indexed automatically on every create and update through the REST API or
the RabbitMQ consumer.

After a direct SQL data import (e.g., the included seed script), call the reindex endpoint
to populate the vector store:

```bash
curl -X POST http://localhost:8080/api/products/reindex \
  -H "Authorization: Bearer <admin-token>"
```

> This calls the OpenAI embeddings API once per product.
> For a 100 product catalog, it completes in a few seconds and costs less than $0.01.

**Seed data:** a ready-to-use SQL script with 10 categories and 100+ computer equipment
products is available at `src/main/resources/db/seed_products.sql`.
Run it via pgAdmin 4 (Query Tool → open file → F5) or via CLI:

```bash
psql -U ecommerce -d ecommerce -f src/main/resources/db/seed_products.sql
```

---

## Product Sync Queue

The application listens on `product.sync.queue` for product events from external systems.
All received products are automatically indexed for semantic search.

### Message format

All messages must be JSON with `Content-Type: application/json`.

| Field           | Type    | Required for | Description                     |
|-----------------|---------|--------------|---------------------------------|
| `action`        | String  | All          | `CREATE`, `UPDATE`, or `DELETE` |
| `sku`           | String  | All          | Unique product identifier       |
| `name`          | String  | CREATE       | Product name (max 255 chars)    |
| `description`   | String  | No           | Product description             |
| `price`         | Decimal | CREATE       | Price, e.g. `29.99`             |
| `stockQuantity` | Integer | CREATE       | Stock quantity (≥ 0)            |
| `imageUrl`      | String  | No           | URL of the product image        |
| `categoryId`    | Long    | No           | Category ID                     |
| `active`        | Boolean | No           | Defaults to `true` on CREATE    |

### Examples

**Create:**
```json
{
  "action": "CREATE",
  "sku": "PROD-001",
  "name": "Wireless Mouse",
  "description": "Ergonomic wireless mouse with USB receiver",
  "price": 39.99,
  "stockQuantity": 200,
  "categoryId": 4
}
```

**Update:**
```json
{
  "action": "UPDATE",
  "sku": "PROD-001",
  "price": 34.99,
  "stockQuantity": 180
}
```

**Delete:**
```json
{
  "action": "DELETE",
  "sku": "PROD-001"
}
```

### Consumer behaviour

| Scenario                      | Behaviour                                                 |
|-------------------------------|-----------------------------------------------------------|
| `CREATE` : SKU is new         | Product created and indexed for semantic search           |
| `CREATE` : SKU already exists | Treated as UPDATE (upsert), embedding refreshed           |
| `UPDATE` : SKU found          | Product updated, embedding refreshed                      |
| `UPDATE` : SKU not found      | Warning logged, message discarded                         |
| `DELETE` : SKU found          | Product deleted, removed from vector index                |
| `DELETE` : SKU not found      | Warning logged, message discarded                         |
| Missing `action` or `sku`     | Warning logged, message discarded                         |
| Unknown `action`              | Warning logged, message discarded                         |

> Partial updates are supported: only non-null fields in an UPDATE message are changed.
