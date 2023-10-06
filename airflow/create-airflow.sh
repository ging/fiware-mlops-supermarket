#!/bin/bash


cd airflow
mkdir dags
mkdir logs
mkdir plugins

cp ../setup.py airflow/dags/
cp ../deploy.py airflow/dags/
