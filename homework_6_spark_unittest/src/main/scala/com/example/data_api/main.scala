package com.example.data_api

import com.example.data_api.taxi_utils.{DistanceDistribution, PopularBorough, PopularTime}
import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.SparkSession

object main extends LazyLogging {
  System.setProperty("hadoop.home.dir", "/")

  val dataPath = "src/main/resources/data"

  val spark: SparkSession = SparkSession.builder()
    .appName("Example")
    .config("spark.master", "local[1]")
    .getOrCreate()

  def main(args: Array[String]): Unit = {
    logger.info(s"Start app: ${spark.sparkContext.appName}")

    val taxiInfoDF = spark.read.parquet(s"$dataPath/new_york_taxi_data")
    val taxiDictDF = spark.read
      .option("header", "true")
      .csv(s"$dataPath/taxi_zones.csv")

    val popularBorough = PopularBorough(taxiInfoDF, taxiDictDF, dataPath)
    popularBorough.show()

    val popularTime = PopularTime(taxiInfoDF, dataPath)
    popularTime.take(20)
      .foreach(println)

    val distanceDistribution = DistanceDistribution(taxiInfoDF, taxiDictDF)
    distanceDistribution.show()

    logger.info(s"Stop app ${spark.sparkContext.appName}")
  }
}
