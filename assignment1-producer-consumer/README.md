# Producer-Consumer Pattern Implementation

A thread-safe producer-consumer implementation demonstrating **thread synchronization**, **blocking queues**, and the **wait/notify mechanism** in Java.

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+

### Run
```bash
# Build and run
mvn compile exec:java -Dexec.mainClass="Main"

# Run tests
mvn test
```

### Configuration
Edit `config.properties`:
```properties
input.file=input.txt
output.file=output.txt
queue.capacity=10
```

---

## Design Overview

```
┌─────────────┐      ┌────────────────┐      ┌─────────────┐
│  INPUT FILE │ ──▶  │ BLOCKING QUEUE │ ──▶  │ OUTPUT FILE │
│  (Source)   │      │   (Shared)     │      │(Destination)│
└─────────────┘      └────────────────┘      └─────────────┘
       │                    ▲  ▲                    │
       │                    │  │                    │
   ┌───▼───┐           wait/notify            ┌─────▼────┐
   │PRODUCER│◀──────────────┴──┴─────────────▶│ CONSUMER │
   │ Thread │                                 │  Thread  │
   └────────┘                                 └──────────┘
```

### Key Components

| Component | Responsibility |
|-----------|----------------|
| `BlockingQueue` | Thread-safe queue with `wait()`/`notifyAll()` for blocking |
| `Producer` | Reads file char-by-char → puts into queue |
| `Consumer` | Takes from queue → writes to output file |

### Thread Synchronization

The blocking behavior is implemented in [`BlockingQueue.java`](src/BlockingQueue.java):
- `put()` - blocks when queue is full using `wait()`, wakes consumers with `notifyAll()`
- `take()` - blocks when queue is empty using `wait()`, wakes producers with `notifyAll()`

---

## Project Structure

```
assignment1-producer-consumer/
├── src/
│   ├── Main.java           # Entry point with ExecutorService
│   ├── Producer.java       # Reads from source file
│   ├── Consumer.java       # Writes to destination file
│   ├── BlockingQueue.java  # Custom blocking queue (wait/notify)
│   ├── AppConfig.java      # Loads config.properties
│   └── model/
│       └── Item.java       # Data wrapper (char)
├── tests/
│   ├── BlockingQueueTest.java  # Queue blocking & synchronization
│   ├── ProducerTest.java       # File reading tests
│   ├── ConsumerTest.java       # File writing tests
│   └── IntegrationTest.java    # End-to-end flow
├── config.properties
├── input.txt
└── pom.xml
```

---

## Test Coverage

**22 tests** covering all assignment objectives:

| Objective | Tests |
|-----------|-------|
| **Blocking Queue** | put/take, capacity, empty/full states, FIFO order |
| **Wait/Notify** | Consumer waits on empty, Producer waits on full |
| **Thread Sync** | Concurrent put/take without data corruption |
| **Concurrency** | Fast producer/slow consumer, slow producer/fast consumer |

Run tests:
```bash
mvn test
```

---

## Assignment Objectives Met
**Thread Synchronization** - `synchronized` methods with `wait()`/`notifyAll()`  
**Concurrent Programming** - Producer and Consumer run as separate threads  
**Blocking Queue** - Custom implementation (not `java.util.concurrent`)  
**Wait/Notify Mechanism** - Threads block and wake based on queue state
