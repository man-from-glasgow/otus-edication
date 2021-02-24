package com.example.ml

import com.typesafe.scalalogging.LazyLogging
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.SparkContext
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
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

    val labelIndexer = new StringIndexer()
      .setInputCol("label")
      .setOutputCol("indexedLabel")
      .fit(irisData)

    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .setMaxCategories(4)
      .fit(irisData)

    val Array(trainingData, testData) = irisData.randomSplit(Array(0.7, 0.3))

    val dt = new DecisionTreeClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("indexedFeatures")

    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(labelIndexer.labelsArray(0))

    val pipeline = new Pipeline()
      .setStages(Array(labelIndexer, featureIndexer, dt, labelConverter))

    val model = pipeline.fit(trainingData)

    val predictions = model.transform(testData)

    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("indexedLabel")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(predictions)

    println(s"Test Error = ${(1.0 - accuracy)}")

    saveModel(model, modelPath)

    logger.info("Stop train model")
  }
}
