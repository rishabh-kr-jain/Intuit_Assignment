"""
Unit Tests for Stream Reader
"""

import unittest
import os
import sys

project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, project_root)

from pyspark.sql import SparkSession
from src.stream_reader import SalesStreamReader
from src.utils.schema import get_sales_schema


class TestStreamReader(unittest.TestCase):
    """Tests for SalesStreamReader and schema."""
    
    @classmethod
    def setUpClass(cls):
        cls.spark = SparkSession.builder \
            .appName("StreamReaderTests") \
            .master("local[*]") \
            .getOrCreate()
        cls.spark.sparkContext.setLogLevel("ERROR")
    
    @classmethod
    def tearDownClass(cls):
        cls.spark.stop()
    
    def test_schema_field_count(self):
        """Schema should have 14 fields."""
        schema = get_sales_schema()
        self.assertEqual(len(schema.fields), 14)
    
    def test_reader_initialization(self):
        """Reader should initialize correctly."""
        reader = SalesStreamReader(self.spark, "dummy_path")
        self.assertIsNotNone(reader.spark)
        self.assertEqual(reader.data_path, "dummy_path")


if __name__ == "__main__":
    unittest.main(verbosity=2)
