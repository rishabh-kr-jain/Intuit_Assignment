"""
Main Application - Sales Data Streaming Analysis

Assignment 2: Demonstrate streaming operations using PySpark
- Functional programming
- Stream operations  
- Data aggregation
- Lambda expressions
"""

import os
import sys

project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, project_root)

from src.utils.spark_session import create_spark_session, stop_spark_session
from src.stream_reader import SalesStreamReader
from src.analysis.sales_analyzer import SalesAnalyzer


def print_header(title: str):
    """Print section header."""
    print("\n" + "=" * 60)
    print(f"  {title}")
    print("=" * 60)


def run_analysis():
    """Run all analyses on sales data."""
    # pass the file path as env variable - TODO
    data_path = os.path.join(project_root, "data/sales_data.csv")
    
    print_header("PYSPARK SALES DATA ANALYSIS")
    print(f"\nData Source: {data_path}")
    
    spark = create_spark_session("SalesDataAnalysis")
    
    try:
        # Load data
        reader = SalesStreamReader(spark, data_path)
        df = reader.read_batch()
        analyzer = SalesAnalyzer(df)
        
        # Overall Statistics
        print_header("OVERALL STATISTICS")
        for key, value in analyzer.get_overall_statistics().items():
            print(f"  {key}: {value}")
        
        # Aggregations
        print_header("1. TOTAL SALES BY REGION")
        print("groupBy, agg, sum, count, avg, orderBy")
        analyzer.total_sales_by_region().show()
        
        print_header("2. TOTAL SALES BY CATEGORY")
        print("groupBy with multiple aggregations")
        analyzer.total_sales_by_category().show()
        
        print_header("3. SALES BY REPRESENTATIVE")
        print("groupBy, aggregations")
        analyzer.sales_by_rep().show()
        
        print_header("4. MONTHLY SALES TREND")
        print("withColumn, year(), month()")
        analyzer.monthly_sales_trend().show(12)
        
        print_header("5. SALES BY CHANNEL")
        print("groupBy, channel comparison")
        analyzer.sales_by_channel().show()
        
        # Lambda Expressions
        print_header("6. HIGH VALUE SALES FILTER")
        print("filter() with column expressions")
        analyzer.filter_high_value_sales(7000).show(10)
        
        print_header("7. SALES CLASSIFICATION")
        print("when/otherwise conditional logic")
        analyzer.apply_sales_classification().show(10)
        
        print_header("8. PROFIT CALCULATION")
        print("withColumn arithmetic expressions")
        analyzer.calculate_profit().show(10)
        
        print_header("9. FILTER AND AGGREGATE")
        print("Method chaining - functional pipeline")
        analyzer.filter_and_aggregate().show()
        
        print_header("ANALYSIS COMPLETE")
        
    finally:
        stop_spark_session(spark)
    
    return 0


if __name__ == "__main__":
    sys.exit(run_analysis())
