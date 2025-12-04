"""
Unit Tests for Sales Data Analysis
Assignment 2: PySpark Streaming Analysis
"""

import unittest
import os
import sys

project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, project_root)

from pyspark.sql import SparkSession
from pyspark.sql.functions import sum as spark_sum
from src.stream_reader import SalesStreamReader
from src.analysis.sales_analyzer import SalesAnalyzer
from src.utils.schema import get_sales_schema


class TestSalesAnalysis(unittest.TestCase):
    """Tests for stream reader and sales analyzer."""
    
    @classmethod
    def setUpClass(cls):
        """Set up SparkSession and test data."""
        cls.spark = SparkSession.builder \
            .appName("SalesAnalysisTests") \
            .master("local[*]") \
            .getOrCreate()
        cls.spark.sparkContext.setLogLevel("ERROR")
        
        # Sample test data
        cls.test_data = [
            (1001, "2023-01-15", "Alice", "North", 5000.00, 10, "Electronics", 300.00, 500.00, "New", 0.10, "Credit Card", "Online", "N-A"),
            (1002, "2023-01-20", "Bob", "South", 3000.00, 5, "Clothing", 200.00, 600.00, "Returning", 0.05, "Cash", "Retail", "S-B"),
            (1003, "2023-02-10", "Alice", "North", 7000.00, 15, "Electronics", 350.00, 500.00, "New", 0.15, "Bank Transfer", "Online", "N-A"),
            (1004, "2023-02-15", "Charlie", "East", 2000.00, 8, "Food", 100.00, 250.00, "Returning", 0.00, "Cash", "Retail", "E-C"),
            (1005, "2023-03-01", "Bob", "South", 8000.00, 20, "Furniture", 250.00, 400.00, "New", 0.20, "Credit Card", "Online", "S-B"),
        ]
        
        cls.df = cls.spark.createDataFrame(cls.test_data, get_sales_schema())
        cls.analyzer = SalesAnalyzer(cls.df)
    
    @classmethod
    def tearDownClass(cls):
        """Stop SparkSession."""
        cls.spark.stop()
    
    # Stream Reader Tests
    
    def test_schema_fields(self):
        """Test schema has correct number of fields."""
        schema = get_sales_schema()
        self.assertEqual(len(schema.fields), 14)
    
    def test_reader_initialization(self):
        """Test reader initializes correctly."""
        reader = SalesStreamReader(self.spark, "dummy_path")
        self.assertIsNotNone(reader.spark)
    
    # Aggregation Tests
    
    def test_total_sales_by_region(self):
        """Test region aggregation."""
        result = self.analyzer.total_sales_by_region()
        data = {row["Region"]: row["Total_Sales"] for row in result.collect()}
        self.assertEqual(data["North"], 12000.00)
        self.assertEqual(data["South"], 11000.00)
    
    def test_total_sales_by_category(self):
        """Test category aggregation."""
        result = self.analyzer.total_sales_by_category()
        self.assertEqual(result.count(), 4)
    
    def test_sales_by_rep(self):
        """Test sales rep aggregation."""
        result = self.analyzer.sales_by_rep()
        data = {row["Sales_Rep"]: row["Total_Sales"] for row in result.collect()}
        self.assertEqual(data["Alice"], 12000.00)
    
    def test_monthly_sales_trend(self):
        """Test monthly trend has required columns."""
        result = self.analyzer.monthly_sales_trend()
        self.assertIn("Year", result.columns)
        self.assertIn("Month", result.columns)
    
    def test_sales_by_channel(self):
        """Test channel aggregation."""
        result = self.analyzer.sales_by_channel()
        channels = [row["Sales_Channel"] for row in result.collect()]
        self.assertIn("Online", channels)
        self.assertIn("Retail", channels)
    
    # Lambda Expression Tests
    
    def test_filter_high_value_sales(self):
        """Test filtering with threshold."""
        result = self.analyzer.filter_high_value_sales(5000.0)
        for row in result.collect():
            self.assertGreater(row["Sales_Amount"], 5000.0)
    
    def test_apply_sales_classification(self):
        """Test sales tier classification."""
        result = self.analyzer.apply_sales_classification()
        data = {row["Sales_Amount"]: row["Sales_Tier"] for row in result.collect()}
        self.assertEqual(data[8000.00], "High")
        self.assertEqual(data[5000.00], "Medium")
        self.assertEqual(data[2000.00], "Low")
    
    def test_calculate_profit(self):
        """Test profit calculation."""
        result = self.analyzer.calculate_profit()
        self.assertIn("Profit", result.columns)
    
    def test_filter_and_aggregate(self):
        """Test filter + aggregate chain."""
        result = self.analyzer.filter_and_aggregate()
        self.assertIn("New_Customer_Sales", result.columns)
    
    # Statistics Test
    
    def test_get_overall_statistics(self):
        """Test overall statistics."""
        stats = self.analyzer.get_overall_statistics()
        self.assertEqual(stats["Total Transactions"], 5)
        self.assertEqual(stats["Min Sale"], 2000.00)
        self.assertEqual(stats["Max Sale"], 8000.00)


if __name__ == "__main__":
    unittest.main(verbosity=2)
