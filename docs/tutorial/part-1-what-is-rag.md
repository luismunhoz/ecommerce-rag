# Building a RAG-Powered eCommerce App in Java. Part 1: What is RAG?

> **Series overview**
> In this series we build a real Spring Boot eCommerce API from scratch and extend it with a full
> Retrieval-Augmented Generation (RAG) pipeline using LangChain4j, pgvector, and OpenAI.
> No prior AI or ML knowledge required.
> 
> _This series of tutorials and the Java project were created with the support of AI._ 
>
> - **Part 1 : What is RAG?** ← you are here
> - Part 2 : Setting Up the Stack: Spring Boot, pgvector & LangChain4j
> - Part 3 : The Indexing Pipeline: Turning Products into Vectors
> - Part 4 : Semantic Search: The Retrieval Step
> - Part 5 : Full RAG: Generating AI Answers with ChatLanguageModel

---

If you are a Java developer who has been hearing words like *embeddings*, *vector databases*, and
*RAG* and feeling left out of the conversation — this series is for you.

We are not going to build a toy demo. We are going to build a production-shaped Spring Boot
eCommerce API for a computer equipment store, and progressively extend it with a full RAG pipeline
that lets users ask questions like:

> *"What laptop should I buy for video editing under $2000?"*

…and receive a grounded, accurate AI answer based on the actual products in the database.

By the end of this series you will understand not only *what* RAG is, but *why* every design
decision in the implementation exists. Let's start from scratch.

---

## The problem with keyword search 

Traditional search works on exact or partial string matches.
If your product database has a laptop called **"Dell XPS 15 9530"** and the user searches for
`"portable workstation for creative professionals"`, a keyword search returns nothing — because none
of those words appear in the product name.

The user knew what they wanted. The system just couldn't understand them.

This is not a problem you can solve by adding more keywords to your database. It is a
*language understanding* problem.

---

## What are embeddings?

An **embedding** is a list of numbers (a vector) that represents the meaning of a piece of text.

```
"Dell XPS 15 laptop"  →  [0.021, -0.145, 0.302, 0.011, ...]  // 1536 numbers
"portable workstation" →  [0.019, -0.139, 0.298, 0.008, ...]  // 1536 numbers
"refrigerator"         →  [-0.201, 0.443, -0.112, 0.377, ...]  // very different
```

The key property is: **texts with similar meaning produce vectors that are close to each other**
in that 1536-dimensional space. The distance between two vectors is a measure of semantic similarity.

You do not need to understand how this works internally to use it. What matters is: you send a
string to an embedding API and get back a vector. Similar strings produce similar vectors.

In this project we use OpenAI's `text-embedding-3-small` model to generate these vectors.
It produces 1536-dimensional embeddings at very low cost.

---

## What is a vector database?

A vector database stores embeddings and lets you query them efficiently.

The query is: *"give me the N stored vectors closest to this query vector"*.
This is called **Approximate Nearest Neighbour (ANN)** search.

We use **pgvector** — a PostgreSQL extension that adds a `vector` column type and a
`<=>` cosine distance operator. This means our regular PostgreSQL database also becomes a
vector database. No extra infrastructure needed.

```sql
-- Find the 5 most similar product embeddings to a query vector
SELECT product_id, 1 - (embedding <=> ?) AS score
FROM product_embeddings
ORDER BY score DESC
LIMIT 5;
```

---

## What is RAG?

**RAG** stands for **Retrieval-Augmented Generation**.

It is a pattern that connects a vector database to a Large Language Model (LLM) to produce
accurate, grounded answers. It has three steps:

```
User question
      │
      ▼
┌─────────────────────────────────────┐
│  1. RETRIEVE                        │
│  Embed the question → query pgvector│
│  → get top-N relevant products      │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│  2. AUGMENT                         │
│  Build a prompt that contains:      │
│  - the retrieved products           │
│  - the user's original question     │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│  3. GENERATE                        │
│  Send the prompt to the LLM         │
│  → receive a grounded answer        │
└─────────────────────────────────────┘
```

### Why not just send the question directly to the LLM?

An LLM trained on internet data knows a lot, but it does not know *your* product catalog.
It cannot know what you have in stock today, at what prices, or what is available.
Without RAG, the LLM would hallucinate — it would invent plausible-sounding but wrong products.

RAG solves this by giving the LLM only the relevant facts it needs to answer the question,
retrieved from your own database. The LLM's job is then just to compose a good answer
from that information. It cannot invent products that were not in the prompt.

### Why not just dump the entire product catalog into the prompt?

LLMs have a **context window limit** — the maximum amount of text they can process in one call.
A catalog of thousands of products would not fit, and even if it did, the cost would be enormous.

RAG retrieves only the *relevant* products — typically 3 to 10 — keeping prompts small,
fast, and cheap.

---

## The project we are building

Over this series we will build an eCommerce API with the following stack:

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Architecture | Hexagonal (Ports & Adapters) |
| Database | PostgreSQL 16 + pgvector extension |
| Message broker | RabbitMQ 3.13 |
| AI/RAG framework | LangChain4j 0.35 |
| Embedding model | OpenAI `text-embedding-3-small` |
| Chat model | OpenAI `gpt-4o-mini` |
| Infrastructure | Docker Compose |
| Frontend | React 18 |

The domain covers products, categories, users, shopping carts, and orders.
On top of this we add two AI features:

1. **Semantic search** — `GET /api/products/semantic-search?q=...`
   Returns products ranked by semantic relevance (the Retrieval step alone).

2. **AI Chat assistant** — `POST /api/chat/ask`
   Full RAG: retrieves relevant products, builds a prompt, generates an AI answer.

---

## What is LangChain4j?

LangChain4j is the Java/Kotlin port of the Python LangChain ecosystem.
It provides:

- Integrations with embedding models (OpenAI, Cohere, HuggingFace, etc.)
- Integrations with chat models (OpenAI, Anthropic, Gemini, etc.)
- Integrations with vector stores (pgvector, Pinecone, Weaviate, Chroma, etc.)
- High-level abstractions for building RAG pipelines

For Java developers it removes all the boilerplate of directly calling REST APIs and gives you
strongly-typed, Spring-friendly beans to work with.

```java
// Embed a piece of text — one method call
Embedding embedding = embeddingModel.embed("Dell XPS 15 laptop").content();

// Query the vector store — one method call
List<EmbeddingMatch<TextSegment>> results =
    embeddingStore.findRelevant(queryEmbedding, 5, 0.65);

// Generate an answer — one method call
String answer = chatLanguageModel.generate(prompt);
```

---

## Key concepts summary

| Concept | What it is | Analogy |
|---|---|---|
| **Embedding** | A vector of numbers representing text meaning | GPS coordinates for meaning |
| **Vector store** | A database optimised for similarity search | A library where books are shelved by topic proximity |
| **Cosine similarity** | How close two vectors are (0 = unrelated, 1 = identical) | Angle between two arrows |
| **RAG** | Retrieve relevant facts → inject into prompt → generate answer | Open-book exam |
| **LangChain4j** | Java framework for building AI applications | Spring Boot for AI |

---

## What comes next

In **Part 2** we set up the full project: Spring Boot, Docker Compose with PostgreSQL + pgvector,
and the LangChain4j dependencies. We will also walk through the Flyway migration that creates
the `product_embeddings` table with a `vector(1536)` column.

If you want to follow along with the code, the full project is available at [github project](https://github.com/luismunhoz/ecommerce-rag).

---

*Published as part of the series: Building a RAG-Powered eCommerce App in Java*
