# Buyogo Backend Internship Assignment

## Overview

This project implements a backend service for ingesting manufacturing events, deduplicating and updating them safely, and exposing analytics endpoints for defect tracking and machine health.

The system is designed with correctness, idempotency, and clear time-bound semantics as first-class concerns.

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- H2 (in-memory database)
- JUnit 5
- Mockito
- Maven

---

## Features

### 1. Batch Event Ingestion
**Endpoint:** `POST /events/batch`

- Accepts a batch of events
- Validates each event
- Handles:
  - New events → accepted
  - Same eventId + identical payload → deduped
  - Same eventId + different payload (newer receivedTime) → updated
  - Same eventId + different payload (older receivedTime) → ignored
- Uses SHA-256 payload hashing for deduplication
- receivedTime is generated on the server
- Returns detailed ingestion statistics

---

### 2. Top Defect Lines
**Endpoint:**  
`GET /stats/top-defect-lines?factoryId=F01&from=...&to=...&limit=10`

- Aggregates events by production line
- Computes:
  - Total defects
  - Event count
  - Defect percentage
- Sorted by highest defect count
- Supports result limiting
- Time range is start-inclusive and end-exclusive

---

### 3. Machine Health Stats
**Endpoint:**  
`GET /stats/machine?machineId=M001&from=...&to=...`

- Computes:
  - Total events
  - Total defects
  - Average defect rate per hour
- Ignores defectCount values less than 0
- Machine status:
  - Healthy → average defect rate < 2/hour
  - Warning → otherwise
- Time range follows:
  - eventTime >= start
  - eventTime < end

---

## Data Model

### EventEntity

Fields:
- eventId (primary key)
- eventTime
- receivedTime
- machineId
- durationMs
- defectCount
- payloadHash

Database index:
```bash
(machineId, eventTime)
```

Note: `machineId` encodes factory and line information in the format `Fxx-Lxx-Mxxx` to enable factory- and line-level analytics without additional schema fields.


---

## Deduplication & Update Logic

- Payload hash is computed using a canonical string and SHA-256
- Deduplication is based on:
  - eventId
  - payloadHash
- Update decision is based on receivedTime ordering

This ensures idempotent and deterministic ingestion.

---

## Time Boundary Semantics

All time-window queries follow:
```bash
start <= eventTime < end
```

This avoids overlap when chaining time windows and ensures consistent analytics.

---

## Thread Safety & Concurrency

- Ingestion runs within transactional boundaries
- eventId is a primary key
- Database constraints prevent duplicate inserts
- Concurrent ingestion behavior is validated using integration tests

Note: Mock-based tests cannot guarantee real thread safety due to lack of persistence and isolation.

---

## Testing

### Covered Test Cases

1. Identical duplicate eventId → deduped  
2. Different payload + newer receivedTime → update happens  
3. Different payload + older receivedTime → ignored  
4. Invalid duration rejected  
5. Future eventTime rejected  
6. defectCount = -1 ignored in defect totals  
7. Start-inclusive / end-exclusive boundary correctness  
8. Concurrent ingestion does not corrupt counts or updates  

Testing includes:
- Unit tests with Mockito
- Integration tests for repository behavior and concurrency

---

## Running the Application

```bash
mvn spring-boot:run

```

---

## H2 Console

- URL: http://localhost:8080/h2-console

- JDBC URL: jdbc:h2:mem:buyogo-db

---

## Design Decisions

- receivedTime is server-generated to avoid client clock manipulation

- Hash-based deduplication ensures deterministic behavior

- Inclusive/exclusive time windows prevent double counting

- Minimal mutable state improves correctness and clarity

---

## Possible Improvements

- Add pagination to analytics endpoints to prevent large result sets and improve API responsiveness

- Replace the in-memory database with a persistent database such as PostgreSQL or MySQL for durability and production readiness

- Introduce optimistic locking to prevent lost updates when multiple concurrent writers modify the same event

- Add basic metrics and observability to monitor ingestion throughput, latency, and error rates in production

## Author
### Jalli Chakravarthi
### Backend Intern Candidate – Buyogo