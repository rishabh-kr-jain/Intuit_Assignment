"""
Unit Tests for Sales Analyzer
"""

import unittest
import os
import sys

project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, project_root)

from pyspark.sql import SparkSession
from src.analysis.sales_analyzer import SalesAnalyzer
from src.utils.schema import get_sales_schema


class TestSalesAnalyzer(unittest.TestCase):
    """Tests for SalesAnalyzer methods."""
    
    @classmethod
    def setUpClass(cls):
        cls.spark = SparkSession.builder \
            .appName("AnalyzerTests") \
            .master("local[*]") \
            .getOrCreate()
        cls.spark.sparkContext.setLogLevel("ERROR")
        
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
        cls.spark.stop()
    
    # Aggregation Tests
    
    def test_total_sales_by_region(self):
        """Region aggregation should return correct totals."""
        result = self.analyzer.total_sales_by_region()
        data = {row["Region"]: row["Total_Sales"] for row in result.collect()}
        self.assertEqual(data["North"], 12000.00)
        self.assertEqual(data["South"], 11000.00)
    
    def test_total_sales_by_category(self):
        """Category aggregation should return 4 categories."""
        result = self.analyzer.total_sales_by_category()
        self.assertEqual(result.count(), 4)
    
    def test_sales_by_rep(self):
        """Sales rep aggregation should return correct totals."""
        result = self.analyzer.sales_by_rep()
        data = {row["Sales_Rep"]: row["Total_Sales"] for row in result.collect()}
        self.assertEqual(data["Alice"], 12000.00)
    
    # Transformation Tests (Lambda Expressions)
    
    def test_filter_high_value_sales(self):
        """Filter should return only sales above threshold."""
        result = self.analyzer.filter_high_value_sales(5000.0)
        for row in result.collect():
            self.assertGreater(row["Sales_Amount"], 5000.0)
    
    def test_apply_sales_classification(self):
        """Classification should assign correct tiers."""
        result = self.analyzer.apply_sales_classification()
        data = {row["Sales_Amount"]: row["Sales_Tier"] for row in result.collect()}
        self.assertEqual(data[8000.00], "High")
        self.assertEqual(data[5000.00], "Medium")
        self.assertEqual(data[2000.00], "Low")
    
    def test_calculate_profit(self):
        """Profit calculation should add Profit column."""
        result = self.analyzer.calculate_profit()
        self.assertIn("Profit", result.columns)
    
    # Statistics Test
    
    def test_get_overall_statistics(self):
        """Overall statistics should return correct values."""
        stats = self.analyzer.get_overall_statistics()
        self.assertEqual(stats["Total Transactions"], 5)
        self.assertEqual(stats["Min Sale"], 2000.00)
        self.assertEqual(stats["Max Sale"], 8000.00)


if __name__ == "__main__":
    unittest.main(verbosity=2)
