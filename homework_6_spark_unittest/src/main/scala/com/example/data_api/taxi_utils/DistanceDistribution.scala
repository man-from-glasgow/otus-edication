package com.example.data_api.taxi_utils

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode}
import org.apache.spark.sql.functions.{col, count, max, mean, min, stddev}

import java.util.Properties

object DistanceDistribution extends LazyLogging {
  val driver = "org.postgresql.Driver"
  val url = "jdbc:postgresql://localhost:5432/otus"
  val user = "otus"
  val password = "otus"
  val table = "distance_distribution"

  val connectionProperties = new Properties()
  connectionProperties.put("user", user)
  connectionProperties.put("password", password)
  connectionProperties.put("driver", driver)

  def apply(taxiInfoDF: DataFrame, taxiDictDF: DataFrame, saveData: Boolean = true): Dataset[Row] = {
    logger.info("Start prepare distance distribution")

    val distanceDistribution = taxiInfoDF.select("DOLocationID", "trip_distance")
      .join(taxiDictDF, taxiInfoDF("DOLocationID") === taxiDictDF("LocationID"))
      .filter(col("trip_distance") > 0)
      .groupBy(taxiDictDF("Borough") as "borough")
      .agg(
        count(taxiDictDF("Borough")) as "count",
        mean(taxiInfoDF("trip_distance")) as "mean_distance",
        stddev(taxiInfoDF("trip_distance")) as "std_distance",
        min(taxiInfoDF("trip_distance")) as "min_distance",
        max(taxiInfoDF("trip_distance")) as "max_distance",
      )
      .sort(col("count").desc)

    logger.info("Stop prepare distance distribution")

    if (saveData) {
      distanceDistribution
        .write
        .mode(SaveMode.Overwrite)
        .jdbc(url, "distance_distribution", connectionProperties)

      logger.info(s"Save data to table: $table")
    }

    distanceDistribution
  }
}
