# Assignment 2: Sales Data Streaming Analysis

PySpark application demonstrating **functional programming**, **data aggregation**, and **lambda expressions** on sales data.

---

## Quick Start

```bash
# Install dependencies
pip install -r requirements.txt

# Run the analysis
python3 -m src.main

# Run unit tests
python3 -m pytest tests/ -v
```

---

## Project Structure & File Descriptions

```
assignment2-streaming_analysis/
├── data/
│   └── sales_data.csv            # Input dataset
├── src/
│   ├── main.py                   # Entry point
│   ├── stream_reader.py          # Reads CSV data using PySpark
│   ├── analysis/
│   │   └── sales_analyzer.py     # Core analysis methods
│   └── utils/
│       ├── schema.py             # DataFrame schema definition
│       └── spark_session.py      # SparkSession configuration
├── tests/
│   └── test_sales_analysis.py    # Unit tests 
├── requirements.txt
└── README.md
```

### File Descriptions

| File | Purpose |
|------|---------|
| `src/main.py` | Entry point. Loads data, runs all analyses, prints results. |
| `src/stream_reader.py` | `SalesStreamReader` class - reads CSV into PySpark DataFrame. |
| `src/analysis/sales_analyzer.py` | `SalesAnalyzer` class - contains 9 analysis methods demonstrating aggregations and lambda expressions. |
| `src/utils/schema.py` | Defines the schema for the sales data. |
| `src/utils/spark_session.py` | Helper to create/stop SparkSession. |
| `tests/test_sales_analysis.py` | unit tests covering all analysis methods. |

---

## Analysis Methods

1. **Total Sales by Region** - Groups sales data by region and calculates totals
2. **Total Sales by Category** - Summarizes sales by product category
3. **Sales by Representative** - Shows performance metrics per sales rep
4. **Monthly Sales Trend** - Analyzes sales patterns over time
5. **Sales by Channel** - Compares Online vs Retail performance
6. **High Value Sales Filter** - Filters transactions above a threshold
7. **Sales Classification** - Categorizes sales into High/Medium/Low tiers
8. **Profit Calculation** - Computes profit from price and cost data
9. **Filter and Aggregate** - Chains multiple operations (filter → group → aggregate)

---

## Output

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

---

## Requirements

- Python 3.8+
- Java 8 or 11 (required for PySpark)
- PySpark 3.4.0+
