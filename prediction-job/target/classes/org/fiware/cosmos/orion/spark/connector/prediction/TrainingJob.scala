package org.fiware.cosmos.orion.spark.connector.prediction

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.feature.{VectorAssembler, VectorIndexer}
import org.apache.spark.ml.regression._
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import java.io.{File,PrintWriter}
import org.mlflow.tracking.{MlflowClient,MlflowClientVersion}
import org.mlflow.tracking.creds.BasicMlflowHostCreds
import org.mlflow.api.proto.Service.{RunStatus, RunInfo}
import org.mlflow.api.proto.ModelRegistry.ModelVersion  
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TrainingJob {
  def main(args: Array[String]): Unit = {
    train()

  }
  def train() {
    val schema = StructType(
      Array(StructField("id", IntegerType),
            StructField("date", StringType),
            StructField("time", IntegerType),
            StructField("items", IntegerType),
            StructField("day", IntegerType),
            StructField("month", IntegerType),
            StructField("year", IntegerType),
            StructField("weekDay", IntegerType)
      ))

    // Initialize MLFlow connection and experiment
    val mlFlowOpsClient = new MlflowClient("http://localhost:5000")
    val experimentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS"))
    val experimentName = s"Experiment_$experimentDateTime"
    val experimentId = mlFlowOpsClient.createExperiment(experimentName)
    val runInfo = mlFlowOpsClient.createRun(experimentId)
    val runId = runInfo.getRunId()
    mlFlowOpsClient.setExperimentTag(experimentId, "description", "Model for predicting supermarket state")
    mlFlowOpsClient.setTag(runId, "createdAt", experimentDateTime)
    

    val spark = SparkSession
      .builder
      .appName("MLOpsTrainingSupermarket")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._

    spark.sparkContext.setLogLevel("WARN")

    // Load and parse the data file, converting it to a DataFrame.
    val data = spark.read.format("csv")
      .schema(schema)
      .option("header", "true")
      .option("delimiter", ",")
      .load("../prediction-job/carrefour_data.csv")
      .drop("id")
      .withColumnRenamed("items","label")

    val assembler = new VectorAssembler()
        .setInputCols(Array("year", "month", "day", "weekDay","time" ))
        .setOutputCol("features")

    // Automatically identify categorical features, and index them.
    var transformedDf = assembler.transform(data)

    // Split the data into training and test sets (30% held out for testing).
    val Array(trainingData, testData) = transformedDf.randomSplit(Array(0.8, 0.2))

    val maxDepth = 30
    val maxBins = 32
    val numTrees = 100

    // Log params in MLFlow
    mlFlowOpsClient.logParam(runId, "maxDepth", maxDepth.toString)
    mlFlowOpsClient.logParam(runId, "maxBins", maxBins.toString)
    mlFlowOpsClient.logParam(runId, "numTrees", numTrees.toString)


    // Train a RandomForest model.
    val rf = new RandomForestRegressor()
      .setLabelCol("label")
      .setFeaturesCol("features")
      .setMaxDepth(maxDepth)
      .setMaxBins(maxBins)
      .setNumTrees(numTrees)

    val featureIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .fit(transformedDf)

    // Chain indexer and forest in a Pipeline.
    val pipeline = new Pipeline()
    .setStages(Array(featureIndexer, rf))
    // Train model. This also runs the indexer.
    val model = pipeline.fit(trainingData)
    // Make predictions.
    val predictions = model.transform(testData)

    // Select (prediction, true label) and compute test error.
    val evaluator = new RegressionEvaluator()
      .setLabelCol("label")
      .setPredictionCol("prediction")

    val rmse = evaluator.setMetricName("rmse").evaluate(predictions)
    val r2 = evaluator.setMetricName("r2").evaluate(predictions)

    // Log metrics in mlflow
    mlFlowOpsClient.logMetric(runId, "rmse", rmse)
    mlFlowOpsClient.logMetric(runId, "r2", r2)

    
    
    val modelPath = "./prediction-job/model"
    val rfModel = model.stages(1).asInstanceOf[RandomForestRegressionModel]
    rfModel.write.overwrite().save(modelPath)

    //Register model
    mlFlowOpsClient.logArtifact(runId, new File(modelPath))
    mlFlowOpsClient.setTerminated(runId, RunStatus.FINISHED, System.currentTimeMillis())

  }
}