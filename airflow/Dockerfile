FROM apache/airflow:2.2.5-python3.7
USER root

ENV BASE_URL=https://archive.apache.org/dist/spark/
ENV SPARK_VERSION=2.4.5
ENV HADOOP_VERSION=2.7

ARG DEBIAN_FRONTEND=noninteractive


RUN apt-get update && \
    apt-get install -y wget

# Install OpenJDK-8
RUN apt-get update && \
    apt-get install software-properties-common -y && \
    apt-get update && \
        wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | sudo apt-key add - && \
        echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | sudo tee /etc/apt/sources.list.d/adoptium.list && \
        sudo apt update && \
        sudo apt install temurin-8-jdk -y
    



RUN wget ${BASE_URL}/spark-${SPARK_VERSION}/spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz \
      && tar -xvzf spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz \
      && mv spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION} /opt/. \
      && rm spark-${SPARK_VERSION}-bin-hadoop${HADOOP_VERSION}.tgz

COPY ./requirements.txt /.
RUN pip install -r /requirements.txt

#USER airflow

# COPY --chown=airflow:root ./dags /opt/airflow/dags

COPY --chown=root:root ./dags /opt/airflow/dags