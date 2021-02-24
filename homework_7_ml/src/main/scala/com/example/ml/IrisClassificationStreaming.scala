package com.example.ml

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.SparkContext
import org.apache.spark.ml.PipelineModel
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{concat_ws, from_csv}
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}

object IrisClassificationStreaming extends LazyLogging {
  def apply(spark: SparkSession, modelPath: String)(implicit sc: SparkContext): Unit = {
    import spark.implicits._

    val model = PipelineModel.load(modelPath)

    val struct = StructType(
        StructField("sepal_length", DoubleType, nullable = true) ::
        StructField("sepal_width", DoubleType, nullable = true) ::
        StructField("petal_length", DoubleType, nullable = true) ::
        StructField("petal_width", DoubleType, nullable = true) ::
        Nil
    )

    val irisData: DataFrame = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:29092")
      .option("subscribe", "input")
      .load()
      .selectExpr("CAST(value AS STRING)")
      .withColumn("struct", from_csv($"value", struct, Map("sep" -> ",")))
      .withColumn("sepal_length", $"struct".getField("sepal_length"))
      .withColumn("sepal_width", $"struct".getField("sepal_width"))
      .withColumn("petal_length", $"struct".getField("petal_length"))
      .withColumn("petal_width", $"struct".getField("petal_width"))
      .drop("value", "struct")

    val prediction = model.transform(irisData)

    val query = prediction
      .select(
        concat_ws(",", $"sepal_length", $"sepal_width", $"petal_length", $"petal_width", $"predictionLabel").as("value")
      )
      .writeStream
      .outputMode("append")
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:29092")
      .option("topic", "prediction")
      .start()

    query.awaitTermination()
  }
}
