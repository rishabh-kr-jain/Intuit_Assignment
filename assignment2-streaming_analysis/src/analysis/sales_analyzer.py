"""
Sales Data Analysis Module
Demonstrates PySpark aggregations and lambda expressions on sales data.
"""

from pyspark.sql import DataFrame
from pyspark.sql.functions import (
    col, sum, avg, count, max, min,
    round, desc, year, month, when
)


class SalesAnalyzer:
    """Performs analytical operations on sales data."""
    
    def __init__(self, df: DataFrame):
        self.df = df


    
    def total_sales_by_region(self) -> DataFrame:
        return self.df.groupBy("Region") \
            .agg(
                round(sum("Sales_Amount"), 2).alias("Total_Sales"),
                count("*").alias("Transaction_Count"),
                round(avg("Sales_Amount"), 2).alias("Avg_Sale")
            ) \
            .orderBy(desc("Total_Sales"))
    
    def total_sales_by_category(self) -> DataFrame:
        return self.df.groupBy("Product_Category") \
            .agg(
                round(sum("Sales_Amount"), 2).alias("Total_Sales"),
                sum("Quantity_Sold").alias("Total_Quantity"),
                count("*").alias("Transaction_Count")
            ) \
            .orderBy(desc("Total_Sales"))
    
    def sales_by_rep(self) -> DataFrame:
        return self.df.groupBy("Sales_Rep") \
            .agg(
                round(sum("Sales_Amount"), 2).alias("Total_Sales"),
                count("*").alias("Transactions"),
                round(avg("Sales_Amount"), 2).alias("Avg_Sale")
            ) \
            .orderBy(desc("Total_Sales"))
    
    def monthly_sales_trend(self) -> DataFrame:
        return self.df \
            .withColumn("Year", year(col("Sale_Date"))) \
            .withColumn("Month", month(col("Sale_Date"))) \
            .groupBy("Year", "Month") \
            .agg(
                round(sum("Sales_Amount"), 2).alias("Monthly_Sales"),
                count("*").alias("Transaction_Count")
            ) \
            .orderBy("Year", "Month")
    
    def sales_by_channel(self) -> DataFrame:
        return self.df.groupBy("Sales_Channel") \
            .agg(
                round(sum("Sales_Amount"), 2).alias("Total_Sales"),
                round(avg("Sales_Amount"), 2).alias("Avg_Sale"),
                count("*").alias("Transactions")
            ) \
            .orderBy(desc("Total_Sales"))
    
    def filter_high_value_sales(self, threshold: float = 5000.0) -> DataFrame:
        """filter() with column expressions (lambda-style)"""
        return self.df.filter(col("Sales_Amount") > threshold) \
            .select("Product_ID", "Sales_Rep", "Region", "Sales_Amount") \
            .orderBy(desc("Sales_Amount"))
    
    def apply_sales_classification(self) -> DataFrame:
        """when/otherwise conditional logic (lambda-like)"""
        return self.df.withColumn(
            "Sales_Tier",
            when(col("Sales_Amount") >= 7000, "High")
            .when(col("Sales_Amount") >= 4000, "Medium")
            .otherwise("Low")
        ).select("Product_ID", "Sales_Rep", "Sales_Amount", "Sales_Tier")
    
    def calculate_profit(self) -> DataFrame:
        """withColumn arithmetic expressions"""
        return self.df.withColumn(
            "Profit", 
            round((col("Unit_Price") - col("Unit_Cost")) * col("Quantity_Sold"), 2)
        ).select(
            "Product_ID", "Product_Category", "Sales_Amount", "Profit"
        ).orderBy(desc("Profit"))
    
    def filter_and_aggregate(self) -> DataFrame:
        """Method chaining - functional pipeline"""
        return self.df \
            .filter(col("Customer_Type") == "New") \
            .groupBy("Region") \
            .agg(
                round(sum("Sales_Amount"), 2).alias("New_Customer_Sales"),
                count("*").alias("New_Customer_Count")
            ) \
            .orderBy(desc("New_Customer_Sales"))
    
    
    def get_overall_statistics(self) -> dict:
        """collect, aggregate functions"""
        stats = self.df.agg(
            round(sum("Sales_Amount"), 2).alias("total_sales"),
            round(avg("Sales_Amount"), 2).alias("avg_sale"),
            count("*").alias("total_transactions"),
            round(min("Sales_Amount"), 2).alias("min_sale"),
            round(max("Sales_Amount"), 2).alias("max_sale")
        ).collect()[0]
        
        return {
            "Total Sales": stats["total_sales"],
            "Average Sale": stats["avg_sale"],
            "Total Transactions": stats["total_transactions"],
            "Min Sale": stats["min_sale"],
            "Max Sale": stats["max_sale"]
        }
