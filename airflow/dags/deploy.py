import sys, os, re
import subprocess
import sys

def install(package):
    subprocess.check_call([sys.executable, "-m", "pip", "install", package])
install("iso8601")
from airflow import DAG
from airflow.operators.bash import BashOperator

from datetime import datetime, timedelta
import iso8601


default_args = {
  'owner': 'airflow',
  'depends_on_past': False,
  'start_date': iso8601.parse_date("2016-12-01"),
  'retries': 3,
  'retry_delay': timedelta(minutes=5),
}

deploy_dag = DAG(
  'deploy_prediction_model',
  default_args=default_args,
  schedule_interval=None
)

# We use the same two commands for all our PySpark tasks
pyspark_bash_command = """
/opt/spark-2.4.5-bin-hadoop2.7/bin/spark-submit --class {{ params.class }}  \
  --conf {{ params.conf }} \
  --master {{ params.master }} \
  {{ params.base_path }}/{{ params.filename }} \
  {{ params.base_path }}
"""

pyspark_date_bash_command = """
/opt/spark-2.4.5-bin-hadoop2.7/bin/spark-submit --master {{ params.master }} \
  {{ params.base_path }}/{{ params.filename }} \
  {{ ts }} {{ params.base_path }}
"""

# Train and persist the classifier model
deploy_classifier_model = BashOperator(
  task_id = "pyspark_train_classifier_model",
  bash_command = pyspark_bash_command,
  params = {
    "master": "spark://spark-master:7077",
    "filename": "prediction-job/target/orion.spark.connector.prediction-1.0.1.jar",
    "class": "org.fiware.cosmos.orion.spark.connector.prediction.PredictionJob",
    "conf": "spark.driver.extraJavaOptions=-Dlog4jspark.root.logger=WARN,console",
    "base_path": "/"
  },
  dag=deploy_dag
)

