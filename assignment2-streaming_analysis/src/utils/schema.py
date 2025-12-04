"""
Schema Definition Module

This module defines the schema for the sales data CSV file.
Defining schema explicitly is a best practice for streaming data
as it avoids schema inference overhead and ensures data consistency.
"""

from pyspark.sql.types import (
    StructType,
    StructField,
    StringType,
    IntegerType,
    DoubleType,
    DateType
)


def get_sales_schema() -> StructType:
    """
    Returns the schema for the sales data CSV.
    
    The schema is defined based on the following columns:
    - Product_ID: Integer - Unique identifier for the product
    - Sale_Date: String - Date of the sale (YYYY-MM-DD format)
    - Sales_Rep: String - Name of the sales representative
    - Region: String - Geographic region (North, South, East, West)
    - Sales_Amount: Double - Total sales amount
    - Quantity_Sold: Integer - Number of units sold
    - Product_Category: String - Category of product (Electronics, Clothing, Food, Furniture)
    - Unit_Cost: Double - Cost per unit
    - Unit_Price: Double - Selling price per unit
    - Customer_Type: String - Type of customer (New, Returning)
    - Discount: Double - Discount percentage applied
    - Payment_Method: String - Payment method used (Cash, Credit Card, Bank Transfer)
    - Sales_Channel: String - Sales channel (Online, Retail)
    - Region_and_Sales_Rep: String - Combined region and sales rep identifier
    
    Returns:
        StructType: Spark SQL schema for sales data
    """
    return StructType([
        StructField("Product_ID", IntegerType(), True),
        StructField("Sale_Date", StringType(), True),
        StructField("Sales_Rep", StringType(), True),
        StructField("Region", StringType(), True),
        StructField("Sales_Amount", DoubleType(), True),
        StructField("Quantity_Sold", IntegerType(), True),
        StructField("Product_Category", StringType(), True),
        StructField("Unit_Cost", DoubleType(), True),
        StructField("Unit_Price", DoubleType(), True),
        StructField("Customer_Type", StringType(), True),
        StructField("Discount", DoubleType(), True),
        StructField("Payment_Method", StringType(), True),
        StructField("Sales_Channel", StringType(), True),
        StructField("Region_and_Sales_Rep", StringType(), True)
    ])
