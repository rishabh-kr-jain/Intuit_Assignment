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

## Sample Output

```
============================================================
  OVERALL STATISTICS
============================================================
  Total Sales: 4892345.67
  Average Sale: 4892.35
  Total Transactions: 1000

============================================================
  1. TOTAL SALES BY REGION
============================================================
+------+-----------+-----------------+--------+
|Region|Total_Sales|Transaction_Count|Avg_Sale|
+------+-----------+-----------------+--------+
| North| 1285432.50|              258| 4982.68|
|  East| 1223456.78|              251| 4874.33|
| South| 1198765.32|              245| 4893.33|
|  West| 1184691.07|              246| 4815.00|
+------+-----------+-----------------+--------+

============================================================
  7. SALES CLASSIFICATION
============================================================
+----------+---------+------------+----------+
|Product_ID|Sales_Rep|Sales_Amount|Sales_Tier|
+----------+---------+------------+----------+
|      1023|    Alice|     8500.00|      High|
|      1045|      Bob|     5200.00|    Medium|
|      1012|  Charlie|     2100.00|       Low|
+----------+---------+------------+----------+
```

---

## Requirements

- Python 3.8+
- Java 8 or 11 (required for PySpark)
- PySpark 3.4.0+
