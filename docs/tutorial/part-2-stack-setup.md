# Building a RAG-Powered eCommerce App in Java. Part 2: Setting Up the Stack

> **Series**
> - Part 1 : What is RAG?
> - **Part 2 : Setting Up the Stack: Spring Boot, pgvector & LangChain4j** ← you are here
> - Part 3 : The Indexing Pipeline: Turning Products into Vectors
> - Part 4 : Semantic Search: The Retrieval Step
> - Part 5 : Full RAG: Generating AI Answers with ChatLanguageModel

---

In Part 1 we covered the concepts. Now we set up the project so you can run the code locally.
By the end of this article you will have Spring Boot, PostgreSQL with the pgvector extension,
RabbitMQ, and LangChain4j all running together.

---

## Project structure

The project uses **hexagonal architecture** (also called Ports & Adapters).
If you are not familiar with it, the key idea is: the domain and application logic live in the
centre and have zero dependencies on frameworks or databases. Frameworks talk to the core
through interfaces (ports), and the implementations (adapters) live in the infrastructure layer.

```
src/main/java/com/ecommerce/
├── domain/
│   ├── model/          # JPA entities: Product, Order, User, Cart…
│   ├── port/
│   │   ├── in/         # Use-case interfaces (CreateOrderUseCase…)
│   │   └── out/        # Repository interfaces (ProductRepository…)
│   └── exception/      # Domain exceptions
│
├── application/
│   ├── service/        # Use-case implementations (ProductService, ChatService…)
│   └── dto/            # Request/response objects
│
└── infrastructure/
    ├── adapter/
    │   ├── in/
    │   │   ├── rest/       # Spring MVC controllers
    │   │   └── messaging/  # RabbitMQ consumer
    │   └── out/
    │       ├── persistence/ # Spring Data JPA adapters
    │       └── payment/     # Stripe adapter (stub)
    └── config/             # Spring @Configuration classes
```

---

## Dependencies: build.gradle.kts

```kotlin
dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-amqp")

    // LangChain4j - the three dependencies you need for RAG
    implementation("dev.langchain4j:langchain4j:0.35.0")           // core abstractions
    implementation("dev.langchain4j:langchain4j-open-ai:0.35.0")   // OpenAI embedding + chat models
    implementation("dev.langchain4j:langchain4j-pgvector:0.35.0")  // pgvector store

    // Database
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core:10.6.0")
    implementation("org.flywaydb:flyway-database-postgresql:10.6.0")

    // JWT, Lombok, Swagger…
}
```

Three LangChain4j artifacts are all you need:

| Artifact | What it provides                                                         |
|---|--------------------------------------------------------------------------|
| `langchain4j` | Core interfaces: `EmbeddingModel`, `ChatLanguageModel`, `EmbeddingStore` |
| `langchain4j-open-ai` | `OpenAiEmbeddingModel`, `OpenAiChatModel` - the OpenAI implementations   |
| `langchain4j-pgvector` | `PgVectorEmbeddingStore` - stores and queries vectors in PostgreSQL      |

---

## Docker Compose

The entire infrastructure runs in Docker. The critical detail is the PostgreSQL image:
you must use `pgvector/pgvector:pg16` instead of the standard `postgres:16` image,
because the standard image does not include the pgvector extension.

```yaml
# docker-compose.yml (relevant services)
services:
  postgres:
    image: pgvector/pgvector:pg16   # ← IMPORTANT: must be this image
    environment:
      POSTGRES_DB: ecommerce
      POSTGRES_USER: ecommerce
      POSTGRES_PASSWORD: ecommerce
    ports:
      - "5432:5432"

  rabbitmq:
    image: rabbitmq:3.13-management
    ports:
      - "5672:5672"    # AMQP
      - "15672:15672"  # Management UI (guest/guest)
```

Start the infrastructure:

```bash
docker compose up -d postgres rabbitmq
```

---

## Database migrations with Flyway

We use Flyway to manage the schema. Migrations run automatically on startup.

```
src/main/resources/db/migration/
├── V1__create_users_table.sql
├── V2__create_categories_table.sql
├── V3__create_products_table.sql
├── V4__create_orders_table.sql
├── V5__create_order_items_table.sql
├── V6__create_shopping_cart_tables.sql
└── V7__product_embeddings.sql      ← the RAG table
```

The products table is straightforward:

```sql
-- V3__create_products_table.sql
CREATE TABLE products (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    description    TEXT,
    price          DECIMAL(19, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    sku            VARCHAR(50) UNIQUE,
    image_url      VARCHAR(500),
    category_id    BIGINT,
    active         BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);
```

The interesting one is V7. This is where pgvector comes in:

```sql
-- V7__product_embeddings.sql
CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE product_embeddings (
    id         UUID PRIMARY KEY,
    product_id BIGINT NOT NULL,
    embedding  vector(1536),    -- 1536 dimensions = text-embedding-3-small output size
    content    TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- HNSW index for fast approximate nearest-neighbour search using cosine distance
CREATE INDEX idx_product_embeddings_hnsw
    ON product_embeddings
    USING hnsw (embedding vector_cosine_ops);
```

Three things to notice:
1. `CREATE EXTENSION IF NOT EXISTS vector` : this enables pgvector in this database.
2. `vector(1536)` : the column type that stores our embeddings. The dimension must match
   the embedding model output size exactly. `text-embedding-3-small` produces 1536 dimensions.
3. The **HNSW index** - this is what makes similarity search fast at scale.
   HNSW (Hierarchical Navigable Small World) is an approximate nearest-neighbour algorithm.
   Without it, every query would do a full table scan. With it, searches are sub-millisecond
   even with millions of rows.

---

## Configuring LangChain4j: EmbeddingConfig

All LangChain4j beans are created in a single Spring `@Configuration` class:

```java
@Configuration
public class EmbeddingConfig {

    @Value("${OPENAI_API_KEY}")
    private String openAiApiKey;

    @Value("${app.rag.chat-model:gpt-4o-mini}")
    private String chatModelName;

    @Value("${app.rag.temperature:0.3}")
    private double temperature;

    // The model that converts text → vector
    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(openAiApiKey)
                .modelName("text-embedding-3-small")
                .build();
    }

    // The model that generates answers (used in full RAG, Part 5)
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName(chatModelName)
                .temperature(temperature)
                .build();
    }

    // The vector store backed by pgvector
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return PgVectorEmbeddingStore.builder()
                .host(dbHost)
                .port(dbPort)
                .database(dbName)
                .user(dbUsername)
                .password(dbPassword)
                .table("product_embeddings")
                .dimension(1536)
                .createTable(false)  // Flyway owns the table, not LangChain4j
                .build();
    }
}
```

`createTable(false)` is a critical detail. LangChain4j can create the embeddings table
automatically, but if you do that you lose control of the schema, you cannot add the HNSW index,
adjust column names, or track migrations in Flyway. Always manage the schema yourself.

---

## application.yml: the RAG configuration block

```yaml
app:
  semantic-search:
    max-results: 10    # Maximum products returned by /semantic-search
    min-score: 0.65    # Cosine similarity threshold. discard results below this

  rag:
    chat-model: gpt-4o-mini   # OpenAI model for answer generation
    temperature: 0.3           # 0.0 = deterministic, 1.0 = creative
    max-context-products: 5    # How many products to inject into the prompt
```

The `min-score: 0.65` is worth discussing. Cosine similarity ranges from 0 (completely unrelated)
to 1 (identical). A threshold of 0.65 filters out weak matches while allowing meaningful
semantic connections. You may need to tune this for your specific domain and embedding model.

The `temperature: 0.3` for the chat model keeps answers factual and consistent.
Higher temperatures produce more creative (and potentially more inaccurate) responses.
For a product recommendation use case, you want low temperature.

---

## Environment variables

```bash
export OPENAI_API_KEY=sk-...
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=ecommerce
export DB_USERNAME=ecommerce
export DB_PASSWORD=ecommerce
export JWT_SECRET=your-secret-here
```

---

## Running the application

```bash
# Start infrastructure
docker compose up -d postgres rabbitmq

# Run the Spring Boot app
./gradlew bootRun

# The API is now available at http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

---

## What comes next

In **Part 3** we dive into the indexing pipeline, the process that converts each product's
name and description into a vector and stores it in pgvector. We will look at `ProductEmbeddingService`,
how embeddings are kept in sync when products are created, updated, or deleted, and how the
product ID is encoded into the embedding UUID to avoid needing a separate metadata lookup.

---

*Published as part of the series: Building a RAG-Powered eCommerce App in Java*
