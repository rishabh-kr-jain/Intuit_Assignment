"""
Spark Session Utility Module

This module provides utility functions for creating and managing SparkSession.
"""

from pyspark.sql import SparkSession


def create_spark_session(app_name: str = "SalesDataAnalysis") -> SparkSession:
    """
    Create and return a SparkSession for the application.
    
    Args:
        app_name: Name of the Spark application
        
    Returns:
        SparkSession: Configured Spark session
    """
    spark = SparkSession.builder \
        .appName(app_name) \
        .master("local[*]") \
        .config("spark.sql.streaming.schemaInference", "true") \
        .getOrCreate()
    
    # Set log level to reduce verbosity
    spark.sparkContext.setLogLevel("WARN")
    
    return spark


def stop_spark_session(spark: SparkSession) -> None:
    """
    Stop the SparkSession gracefully.
    
    Args:
        spark: SparkSession to stop
    """
    if spark is not None:
        spark.stop()
