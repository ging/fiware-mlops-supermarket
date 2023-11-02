# FIWARE Machine Learning and MLOps - Supermarket example

First time:
```
docker compose -f docker-compose.maven.yml up -d
```

Start base infraestructure
```
docker compose up -d
```

```
cd airflow
docker compose up -d
```

## With Airflow as orchestrator

- Access http://localhost:5000 to access MLFlow client

- Access http://localhost:3000 to watch the web

- Access http://localhost:8081 to access the Spark Cluste UI client

- Access http://localhost:8080 to access the Airflow Web UI


Run airflow flows from the web (first training then predicting)


## Without Airflow as orchestrator


Start training job
```
docker compose -f docker-compose.submit_train.yml up -d
```

Start prediction job
```
docker compose -f docker-compose.submit_predict.yml up -d
```

Train again
Start training job
```
docker compose -f docker-compose.submit_train.yml up -d
```
