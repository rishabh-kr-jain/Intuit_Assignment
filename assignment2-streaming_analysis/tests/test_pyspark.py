"""
Unit Tests for PySpark Operations
"""

import unittest
import os
import sys

project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, project_root)

from pyspark.sql import SparkSession
from src.utils.schema import get_sales_schema


class TestPySparkOperations(unittest.TestCase):
    """Tests for PySpark DataFrame operations."""
    
    @classmethod
    def setUpClass(cls):
        cls.spark = SparkSession.builder \
            .appName("PySparkTests") \
            .master("local[*]") \
            .getOrCreate()
        cls.spark.sparkContext.setLogLevel("ERROR")
        
        cls.test_data = [
            (1001, "2023-01-15", "Alice", "North", 5000.00, 10, "Electronics", 300.00, 500.00, "New", 0.10, "Credit Card", "Online", "N-A"),
            (1002, "2023-01-20", "Bob", "South", 3000.00, 5, "Clothing", 200.00, 600.00, "Returning", 0.05, "Cash", "Retail", "S-B"),
            (1003, "2023-02-10", "Alice", "North", 7000.00, 15, "Electronics", 350.00, 500.00, "New", 0.15, "Bank Transfer", "Online", "N-A"),
        ]
        cls.df = cls.spark.createDataFrame(cls.test_data, get_sales_schema())
    
    @classmethod
    def tearDownClass(cls):
        cls.spark.stop()
    
    def test_dataframe_creation(self):
        """DataFrame should be created with correct row count."""
        self.assertEqual(self.df.count(), 3)
    
    def test_dataframe_columns(self):
        """DataFrame should have all 14 columns."""
        self.assertEqual(len(self.df.columns), 14)
    
    def test_groupby_aggregation(self):
        """GroupBy aggregation should work correctly."""
        result = self.df.groupBy("Region").count().collect()
        data = {row["Region"]: row["count"] for row in result}
        self.assertEqual(data["North"], 2)
        self.assertEqual(data["South"], 1)
    
    def test_filter_operation(self):
        """Filter operation should work correctly."""
        result = self.df.filter(self.df["Sales_Amount"] > 4000)
        self.assertEqual(result.count(), 2)


if __name__ == "__main__":
    unittest.main(verbosity=2)
