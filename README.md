# FIWARE Machine Learning and MLOps - Supermarket example

1. Instructions

Start base infraestructure
```
docker compose up -d
```


Start training job
```
docker compose -f docker-compose.submit_train.yml up -d
```


Start prediction job
```
docker compose -f docker-compose.submit_predict.yml up -d
```

- Access http://localhost:5000 to access MLFlow client

- Access http://localhost:3000 to watch the web

- Access http://localhost:8080 to access the Spark Cluste UI client


Train again
Start training job
```
docker compose -f docker-compose.submit_train.yml up -d
```