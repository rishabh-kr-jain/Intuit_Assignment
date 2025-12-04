"""
Stream Reader Module

Handles reading CSV data using PySpark.
"""

from pyspark.sql import SparkSession, DataFrame
from src.utils.schema import get_sales_schema


class SalesStreamReader:
    """Reads sales data from CSV files."""
    
    def __init__(self, spark: SparkSession, data_path: str):
        self.spark = spark
        self.schema = get_sales_schema()
        self.data_path = data_path
    
    def read_batch(self) -> DataFrame:
        """Read CSV file as DataFrame."""
        return self.spark.read \
            .format("csv") \
            .option("header", "true") \
            .schema(self.schema) \
            .load(self.data_path)
