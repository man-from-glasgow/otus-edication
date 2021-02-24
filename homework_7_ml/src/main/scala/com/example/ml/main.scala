package com.example.ml

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

import scala.io.Source

object main extends LazyLogging {
  def getResourceFile(filePath: String): Iterator[String] = {
    val file = getClass.getResourceAsStream(filePath)
    Source.fromInputStream(file).getLines()
  }

  def main(args: Array[String]): Unit = {
    System.setProperty("hadoop.home.dir", "/")

    val spark: SparkSession = SparkSession.builder()
      .appName("Structured Streaming")
      .config("spark.master", "local[1]")
      .getOrCreate()

    val dataPath = "src/main/resources/data"
    val modelPath = "src/main/resources/model"

    implicit val sc: SparkContext = spark.sparkContext

    if(args.headOption.isDefined) {
      val action = args(0)
      if (action == "train-model") {
        IrisClassificationTrainModel(spark, dataPath ,modelPath)
      }
    } else {
      IrisClassificationStreaming(spark, modelPath)
    }
  }
}
