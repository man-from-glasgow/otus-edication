package com.example.ml

import com.typesafe.scalalogging.LazyLogging
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkContext
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.sql.SparkSession

object IrisClassificationTrainModel extends LazyLogging {
  def saveModel(model: PipelineModel, modelPath: String)(implicit sc: SparkContext): Unit = {
    val fs = FileSystem.get(sc.hadoopConfiguration)
    val path = new Path(modelPath)
    if (fs.exists(path)) {
      logger.info("Delete model path")
      fs.delete(path, true)
    }

    logger.info("Save model")
    model.save(modelPath)
  }

  def apply(spark: SparkSession, dataPath: String, modelPath: String)(implicit sc: SparkContext): Unit = {
    logger.info("Start train model")

    val irisData = spark.read
      .format("libsvm")
      .load(s"$dataPath/iris_libsvm.txt")

    val Array(trainingData, testData) = irisData.randomSplit(Array(0.7, 0.3))

    val dt = new DecisionTreeClassifier()
      .setLabelCol("label")
      .setFeaturesCol("features")

    val pipeline = new Pipeline()
      .setStages(Array(dt))

    val model = pipeline.fit(trainingData)

    val predictions = model.transform(testData)

    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(predictions)

    println(s"Test Error = ${(1.0 - accuracy)}")

    saveModel(model, modelPath)

    logger.info("Stop train model")
  }
}
