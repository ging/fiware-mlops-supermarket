#! /bin/bash

FILE="./prediction-job/target/orion.spark.connector.prediction-1.0.1.jar"

# Loop until the file exists
while [ ! -f "$FILE" ]; do
  echo "Waiting while .jar is generated."
  sleep 5
done

echo "Submiting the prediction job"


/spark/bin/spark-submit  --class  org.fiware.cosmos.orion.spark.connector.prediction.PredictionJob --master  spark://spark-master:7077 ./prediction-job/target/orion.spark.connector.prediction-1.0.1.jar --conf "spark.driver.extraJavaOptions=-Dlog4jspark.root.logger=WARN,console"

