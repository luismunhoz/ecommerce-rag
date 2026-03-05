-- Enable pgvector extension (requires pgvector/pgvector:pg16 image)
CREATE EXTENSION IF NOT EXISTS vector;

-- Embeddings table managed by Flyway (LangChain4j configured with createTable=false)
-- Schema matches LangChain4j's PgVectorEmbeddingStore expectations
CREATE TABLE IF NOT EXISTS product_embeddings (
    embedding_id UUID        PRIMARY KEY,
    embedding    vector(1536) NOT NULL,
    text         TEXT,
    metadata     JSON
);

-- HNSW index for fast approximate nearest-neighbour cosine similarity search
CREATE INDEX IF NOT EXISTS product_embeddings_hnsw_idx
    ON product_embeddings
    USING hnsw (embedding vector_cosine_ops);
