package com.example.data_api.taxi_utils

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.{DataFrame, Dataset, Row}
import org.apache.spark.sql.functions.{col, count}

object PopularBorough extends LazyLogging {
  def apply(taxiInfoDF: DataFrame, taxiDictDF: DataFrame, dataPath: String): Dataset[Row] = {
    logger.info("Start prepare popular borough")

    val popularBorough = taxiInfoDF.select("DOLocationID")
      .join(taxiDictDF, taxiInfoDF("DOLocationID") === taxiDictDF("LocationID"))
      .groupBy(taxiDictDF("Borough"))
      .agg(count(taxiDictDF("Borough")) as "cnt")
      .sort(col("cnt").desc)

    val folder = s"$dataPath/popular_borough"

    popularBorough.repartition(1)
      .write
      .format("parquet")
      .mode("overwrite")
      .save(folder)

    logger.info(s"Create folder: $folder")

    logger.info("Stop prepare popular borough")

    popularBorough
  }
}
