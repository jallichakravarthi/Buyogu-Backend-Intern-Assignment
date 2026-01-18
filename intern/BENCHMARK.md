# Benchmark

This document describes the performance benchmark conducted for the batch ingestion feature.

---

## System Specifications

- **Machine:** Mac mini (2023)
- **Chip:** Apple M2
- **CPU:** 8-core Apple Silicon (M2)
- **RAM:** 8 GB
- **OS:** macOS Sequoia 15.5
- **Java Version:** OpenJDK 21
- **Database:** H2 (in-memory)

---

## Benchmark Setup

The benchmark measures the time taken to ingest a single batch of **1000 events** using the batch ingestion endpoint.

- All events were valid
- Each event had a unique `eventId`
- Deduplication, hashing, validation, and persistence logic were enabled
- The application was running locally

Timing was measured at the service layer using `System.nanoTime()` to capture end-to-end processing time.

---

## Command Used

The application was started using:

```bash
mvn spring-boot:run
```
The benchmark was executed using a JUnit-based performance test that invokes the batch ingestion service directly.

---

## Measured Results

### Batch Size: 1000 Events

- Total Processing Time: ~79 ms

- Average Time per Event: ~0.079 ms

### This timing includes:

- Input validation

- Payload hashing (SHA-256)

- Deduplication checks

- Database persistence

---

## Optimizations Attempted

- Used SHA-256 hashing over a canonical payload representation to enable constant-time detection of duplicate and updated events

- Generated `receivedTime` on the server to ensure consistent ordering and avoid issues caused by client clock skew

- Added a composite index on `(machineId, eventTime)` to optimize time-range analytics queries

- Delegated time-based filtering to repository queries to reduce in-memory processing and leverage database indexing

- Implemented batch ingestion with linear time complexity `O(n)` and minimal mutable state to ensure predictable performance at scale

---

## Notes

-Results were obtained using an in-memory H2 database; performance may vary with a persistent database

-No JVM tuning or database-level optimizations were applied

-The benchmark focuses on correctness-aware performance rather than maximum throughput