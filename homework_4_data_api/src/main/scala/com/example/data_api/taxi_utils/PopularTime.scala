package com.example.data_api.taxi_utils

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame

import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, LocalTime}

object PopularTime extends LazyLogging {
  private def prepareStamp(stamp: String): LocalTime = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
    LocalDateTime.parse(stamp, formatter).toLocalTime
  }

  def apply(taxiInfoDF: DataFrame, dataPath: String): RDD[(LocalTime, Int)] = {
    logger.info("Start prepare popular time")

    val popularTime =
      taxiInfoDF.rdd
        .map(trip => (prepareStamp(trip(1).toString), 1))
        .reduceByKey(_ + _)
        .sortBy(_._2, ascending = false)

    popularTime.take(20).foreach(println)

    val folder = s"$dataPath/popular_time"

    popularTime.repartition(1)
      .map(row => row._1.toString + " " + row._2.toString)
      .saveAsTextFile(folder)

    logger.info(s"Create folder: $folder")

    logger.info("Stop prepare popular time")

    popularTime
  }
}
