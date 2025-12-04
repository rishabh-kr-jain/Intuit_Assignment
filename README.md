# Intuit Build Challenge

Solutions for two programming assignments demonstrating **thread synchronization** and **data streaming analysis**.

---

## Assignments

| # | Assignment | Description | Stack |
|---|------------|-------------|-------|
| 1 | [Producer-Consumer](#assignment-1-producer-consumer-pattern) | Thread synchronization with blocking queue | Java 21, Maven |
| 2 | [Sales Analysis](#assignment-2-sales-data-streaming-analysis) | PySpark aggregation & functional programming | Python 3.11, PySpark |

---

## Assignment 1: Producer-Consumer Pattern

Thread-safe producer-consumer implementation demonstrating **thread synchronization**, **blocking queues**, and **wait/notify mechanism**.

### Design

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

| Component | File | Responsibility |
|-----------|------|----------------|
| BlockingQueue | [src/BlockingQueue.java](assignment1-producer-consumer/src/BlockingQueue.java) | Thread-safe queue with `wait()`/`notifyAll()` |
| Producer | [src/Producer.java](assignment1-producer-consumer/src/Producer.java) | Reads file char-by-char → puts into queue |
| Consumer | [src/Consumer.java](assignment1-producer-consumer/src/Consumer.java) | Takes from queue → writes to output file |

### Run

```bash
cd assignment1-producer-consumer

# Run application
mvn compile exec:java -Dexec.mainClass="Main"

# Run tests (22 tests)
mvn test

# Run specific test file
mvn test -Dtest=BlockingQueueTest
mvn test -Dtest=ProducerTest
mvn test -Dtest=ConsumerTest
mvn test -Dtest=IntegrationTest
```

### Configuration

Edit `config.properties`:
```properties
input.file=input.txt
output.file=output.txt
queue.capacity=10
```

### Test Coverage

| Test File | Objective |
|-----------|-----------|
| [BlockingQueueTest](assignment1-producer-consumer/tests/BlockingQueueTest.java) | Thread sync, blocking queue, wait/notify |
| [ProducerTest](assignment1-producer-consumer/tests/ProducerTest.java) | File reading, queue interaction |
| [ConsumerTest](assignment1-producer-consumer/tests/ConsumerTest.java) | Queue consumption, file writing |
| [IntegrationTest](assignment1-producer-consumer/tests/IntegrationTest.java) | End-to-end data flow |

### Objectives Met

**Thread Synchronization** - `synchronized` methods with `wait()`/`notifyAll()`  
**Concurrent Programming** - Producer and Consumer as separate threads  
**Blocking Queue** - Custom implementation (not `java.util.concurrent`)  
**Wait/Notify Mechanism** - Threads block and wake based on queue state

---

## Assignment 2: Sales Data Streaming Analysis

PySpark application demonstrating **functional programming**, **data aggregation**, and **lambda expressions**.

### Key Components

| Component | File | Responsibility |
|-----------|------|----------------|
| StreamReader | [src/stream_reader.py](assignment2-streaming_analysis/src/stream_reader.py) | Reads CSV into PySpark DataFrame |
| SalesAnalyzer | [src/analysis/sales_analyzer.py](assignment2-streaming_analysis/src/analysis/sales_analyzer.py) | 9 analysis methods with aggregations |
| Schema | [src/utils/schema.py](assignment2-streaming_analysis/src/utils/schema.py) | DataFrame schema definition |

### Run

```bash
cd assignment2-streaming_analysis

# Install dependencies
pip install -r requirements.txt

# Run analysis
python3 -m src.main

# Run tests
python3 -m unittest discover tests/ -v

# Run specific test file
python3 -m unittest tests/test_stream_reader.py -v
python3 -m unittest tests/test_pyspark.py -v
python3 -m unittest tests/test_analyzer.py -v
```

### Analysis Methods

| # | Method | PySpark Operations |
|---|--------|-------------------|
| 1 | Total Sales by Region | `groupBy`, `agg`, `sum`, `count`, `avg` |
| 2 | Total Sales by Category | `groupBy` with multiple aggregations |
| 3 | Sales by Representative | `groupBy`, aggregations |
| 4 | Monthly Sales Trend | `withColumn`, `year()`, `month()` |
| 5 | Sales by Channel | `groupBy`, channel comparison |
| 6 | High Value Sales Filter | `filter()` with column expressions |
| 7 | Sales Classification | `when`/`otherwise` conditional logic |
| 8 | Profit Calculation | `withColumn` arithmetic expressions |
| 9 | Filter and Aggregate | Method chaining - functional pipeline |

###  Output

```
============================================================
  OVERALL STATISTICS
============================================================
  Total Sales: 5019265.23
  Average Sale: 5019.27
  Total Transactions: 1000
  Min Sale: 100.12
  Max Sale: 9989.04

============================================================
  1. TOTAL SALES BY REGION
============================================================
groupBy, agg, sum, count, avg, orderBy
+------+-----------+-----------------+--------+
|Region|Total_Sales|Transaction_Count|Avg_Sale|
+------+-----------+-----------------+--------+
| North| 1369612.51|              267| 5129.63|
|  East| 1259792.93|              263| 4790.09|
|  West| 1235608.93|              244| 5063.97|
| South| 1154250.86|              226|  5107.3|
+------+-----------+-----------------+--------+


============================================================
  2. TOTAL SALES BY CATEGORY
============================================================
groupBy with multiple aggregations
+----------------+-----------+--------------+-----------------+
|Product_Category|Total_Sales|Total_Quantity|Transaction_Count|
+----------------+-----------+--------------+-----------------+
|        Clothing| 1313474.36|          6922|              268|
|       Furniture| 1260517.69|          6729|              260|
|     Electronics| 1243499.64|          6096|              246|
|            Food| 1201773.54|          5608|              226|
+----------------+-----------+--------------+-----------------+


============================================================
  3. SALES BY REPRESENTATIVE
============================================================
groupBy, aggregations
+---------+-----------+------------+--------+
|Sales_Rep|Total_Sales|Transactions|Avg_Sale|
+---------+-----------+------------+--------+
|    David| 1141737.36|         222| 5142.96|
|      Bob| 1080990.63|         208| 5197.07|
|      Eve|  970183.99|         209| 4642.03|
|    Alice|  965541.77|         192| 5028.86|
|  Charlie|  860811.48|         169| 5093.56|
+---------+-----------+------------+--------+


============================================================
  4. MONTHLY SALES TREND
============================================================
withColumn, year(), month()
+----+-----+-------------+-----------------+
|Year|Month|Monthly_Sales|Transaction_Count|
+----+-----+-------------+-----------------+
|2023|    1|    476092.36|              100|
|2023|    2|    368919.36|               75|
|2023|    3|    402638.77|               80|
|2023|    4|    438992.61|               81|
|2023|    5|    389078.76|               72|
|2023|    6|    418458.34|               92|
|2023|    7|    374242.88|               68|
|2023|    8|    443171.28|               93|
|2023|    9|     367837.6|               68|
|2023|   10|    460378.78|               88|
|2023|   11|     467482.9|               95|
|2023|   12|    392643.58|               85|
+----+-----+-------------+-----------------+
only showing top 12 rows

============================================================
  5. SALES BY CHANNEL
============================================================
groupBy, channel comparison
+-------------+-----------+--------+------------+
|Sales_Channel|Total_Sales|Avg_Sale|Transactions|
+-------------+-----------+--------+------------+
|       Retail|  2560431.3| 5000.84|         512|
|       Online| 2458833.93| 5038.59|         488|
+-------------+-----------+--------+------------+


============================================================
  6. HIGH VALUE SALES FILTER
============================================================
filter() with column expressions
+----------+---------+------+------------+
|Product_ID|Sales_Rep|Region|Sales_Amount|
+----------+---------+------+------------+
|      1036|    David| North|     9989.04|
|      1050|    David| North|     9976.52|
|      1079|    Alice| North|     9972.66|
|      1075|  Charlie| North|     9972.11|
|      1016|    David| South|     9961.96|
|      1063|      Bob| North|     9956.75|
|      1099|    Alice|  East|     9948.71|
|      1089|      Eve|  West|     9933.22|
|      1015|    David| South|     9914.15|
|      1010|  Charlie| North|     9907.72|
+----------+---------+------+------------+
only showing top 10 rows

============================================================
  7. SALES CLASSIFICATION
============================================================
when/otherwise conditional logic
+----------+---------+------------+----------+
|Product_ID|Sales_Rep|Sales_Amount|Sales_Tier|
+----------+---------+------------+----------+
|      1052|      Bob|     5053.97|    Medium|
|      1093|      Bob|     4384.02|    Medium|
|      1015|    David|     4631.23|    Medium|
|      1072|      Bob|     2167.94|       Low|
|      1061|  Charlie|      3750.2|       Low|
|      1021|  Charlie|     3761.15|       Low|
|      1083|      Bob|      618.31|       Low|
|      1087|      Eve|     7698.92|      High|
|      1075|    David|     4223.39|    Medium|
|      1075|  Charlie|     8239.58|      High|
+----------+---------+------------+----------+
only showing top 10 rows

============================================================
  8. PROFIT CALCULATION
============================================================
withColumn arithmetic expressions
+----------+----------------+------------+--------+
|Product_ID|Product_Category|Sales_Amount|  Profit|
+----------+----------------+------------+--------+
|      1008|     Electronics|     3197.78|23441.11|
|      1090|     Electronics|     9702.27|23353.92|
|      1066|            Food|      7391.7|22857.04|
|      1012|        Clothing|     2019.24|22464.12|
|      1035|            Food|     3780.22|22108.95|
|      1060|        Clothing|     3224.71|21649.32|
|      1061|        Clothing|     1990.17|21613.05|
|      1014|            Food|      823.51|21574.52|
|      1050|       Furniture|     2254.91|21322.35|
|      1081|     Electronics|     2896.71|21204.48|
+----------+----------------+------------+--------+
only showing top 10 rows

============================================================
  9. FILTER AND AGGREGATE
============================================================
Method chaining - functional pipeline
+------+------------------+------------------+
|Region|New_Customer_Sales|New_Customer_Count|
+------+------------------+------------------+
| North|         762511.35|               145|
|  East|         678988.09|               138|
|  West|         546284.76|               117|
| South|          518474.1|               104|
+------+------------------+------------------+


============================================================
  ANALYSIS COMPLETE
============================================================
```

### Test Coverage

| Test File | Component |
|-----------|-----------|
| [test_stream_reader.py](assignment2-streaming_analysis/tests/test_stream_reader.py) | Schema & CSV reading |
| [test_pyspark.py](assignment2-streaming_analysis/tests/test_pyspark.py) | DataFrame operations |
| [test_analyzer.py](assignment2-streaming_analysis/tests/test_analyzer.py) | Aggregations & transformations |

### Objectives Met

**Functional Programming** - Lambda expressions, method chaining  
**Data Aggregation** - `groupBy`, `sum`, `avg`, `count`  
**Lambda Expressions** - Filters, transformations, classifications  
**PySpark Streaming** - DataFrame operations on sales data

---

## Project Structure

```
Intuit_Assignment/
├── README.md
├── .gitignore
│
├── assignment1-producer-consumer/
│   ├── src/
│   │   ├── Main.java
│   │   ├── Producer.java
│   │   ├── Consumer.java
│   │   ├── BlockingQueue.java
│   │   ├── AppConfig.java
│   │   └── model/Item.java
│   ├── tests/
│   │   ├── BlockingQueueTest.java
│   │   ├── ProducerTest.java
│   │   ├── ConsumerTest.java
│   │   └── IntegrationTest.java
│   ├── config.properties
│   ├── input.txt
│   └── pom.xml
│
└── assignment2-streaming_analysis/
    ├── src/
    │   ├── main.py
    │   ├── stream_reader.py
    │   ├── analysis/sales_analyzer.py
    │   └── utils/
    │       ├── schema.py
    │       └── spark_session.py
    ├── tests/
    │   ├── test_stream_reader.py
    │   ├── test_pyspark.py
    │   └── test_analyzer.py
    ├── data/sales_data.csv
    └── requirements.txt
```

---

## Requirements

| Assignment | Prerequisites |
|------------|---------------|
| Assignment 1 | Java 21+, Maven 3.8+ |
| Assignment 2 | Python 3.8+, Java 8/11 (for PySpark) |