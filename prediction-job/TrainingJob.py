from pyspark.ml import Pipeline
from pyspark.ml.evaluation import RegressionEvaluator
from pyspark.ml.feature import VectorAssembler, VectorIndexer
from pyspark.ml.regression import RandomForestRegressor
from pyspark.sql import SparkSession
from pyspark.sql.types import IntegerType, StringType, StructField, StructType
import mlflow
from mlflow.entities import RunStatus
from mlflow import MlflowClient
import mlflow.tracking
import mlflow.spark
import os
import datetime

#MASTER = os.environ.get("SPARK_MASTER", "local[*]")
MASTER = "local[*]"
MLFLOW_HOST = os.environ.get("MLFLOW_HOST", "localhost")
CSV_FILE = os.environ.get("CSV_FILE", "carrefour_data_v0.csv")
MLFLOW_URI = "http://"+MLFLOW_HOST+":5000"

def train():
    schema = StructType([
        StructField("id", IntegerType()),
        StructField("date", StringType()),
        StructField("time", IntegerType()),
        StructField("items", IntegerType()),
        StructField("day", IntegerType()),
        StructField("month", IntegerType()),
        StructField("year", IntegerType()),
        StructField("weekDay", IntegerType())
    ])

    # Initialize MLFlow connection and experiment

    
    mlflow.set_experiment_tag("createdAt", datetime.datetime.now().strftime("%Y-%m-%d_%H-%M-%S-%f"))

    spark = SparkSession.builder.appName("MLOpsTrainingSupermarket").master(MASTER).getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    # Load and parse the data file, converting it to a DataFrame.
    data = spark.read.format("csv") \
        .schema(schema) \
        .option("header", "true") \
        .option("delimiter", ",") \
        .load("./prediction-job/"+CSV_FILE) \
        .drop("id") \
        .withColumnRenamed("items", "label")

    assembler = VectorAssembler() \
        .setInputCols(["year", "month", "day", "weekDay", "time"]) \
        .setOutputCol("features")

    transformed_df = assembler.transform(data)

    # Split the data into training and test sets (30% held out for testing).
    training_data, test_data = transformed_df.randomSplit([0.8, 0.2])

    max_depth = 30
    max_bins = 32
    num_trees = 100

    mlflow.log_param("maxDepth", str(max_depth))
    mlflow.log_param("maxBins", str(max_bins))
    mlflow.log_param("numTrees", str(num_trees))

    # Train a RandomForest model.
    rf = RandomForestRegressor() \
        .setLabelCol("label") \
        .setFeaturesCol("features") \
        .setMaxDepth(max_depth) \
        .setMaxBins(max_bins) \
        .setNumTrees(num_trees)

    feature_indexer = VectorIndexer() \
        .setInputCol("features") \
        .setOutputCol("indexedFeatures") \
        .fit(transformed_df)

    # Chain indexer and forest in a Pipeline.
    pipeline = Pipeline() \
        .setStages([feature_indexer, rf])

    # Train model. This also runs the indexer.
    model = pipeline.fit(training_data)
    # Make predictions.
    predictions = model.transform(test_data)

    # Select (prediction, true label) and compute test error.
    evaluator = RegressionEvaluator() \
        .setLabelCol("label") \
        .setPredictionCol("prediction")

    rmse = evaluator.evaluate(predictions, {evaluator.metricName : "rmse"})
    r2 = evaluator.evaluate(predictions, {evaluator.metricName : "r2"})

    mlflow.log_metric("rmse", rmse)
    mlflow.log_metric("r2", r2)

    model_path = "./prediction-job/model"
    rf_model = model.stages[1]

    # Register model
    model_name = 'parkingModel'
    mlflow.spark.log_model(spark_model=rf_model, artifact_path=model_name, registered_model_name=model_name)

    client = MlflowClient(tracking_uri=MLFLOW_URI)
    model_version = client.get_latest_versions(model_name, stages=["None"])[0].version
    rf_model.write().overwrite().save(model_path+"/"+model_version)



if __name__ == "__main__":
    mlflow.set_tracking_uri(MLFLOW_URI)
    mlflow.set_experiment("Experiment")
    experiment = mlflow.get_experiment_by_name("Experiment")
    experiment_id = experiment.experiment_id
    with mlflow.start_run(experiment_id=experiment_id, description="Model for predicting supermarket state") as run:
        train()