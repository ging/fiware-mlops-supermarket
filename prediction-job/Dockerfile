FROM ubuntu:20.04

RUN apt-get update && apt-get install -y --no-install-recommends \
		bzip2 \
		unzip \
		xz-utils \
	&& rm -rf /var/lib/apt/lists/*

# Default to UTF-8 file.encoding
ENV LANG C.UTF-8

# add a simple script that can auto-detect the appropriate JAVA_HOME value
# based on whether the JDK or only the JRE is installed
RUN { \
		echo '#!/bin/sh'; \
		echo 'set -e'; \
		echo; \
		echo 'dirname "$(dirname "$(readlink -f "$(which javac || which java)")")"'; \
	} > /usr/local/bin/docker-java-home \
	&& chmod +x /usr/local/bin/docker-java-home

# do some fancy footwork to create a JAVA_HOME that's cross-architecture-safe
RUN ln -svT "/usr/lib/jvm/java-8-openjdk-$(dpkg --print-architecture)" /docker-java-home
ENV JAVA_HOME /docker-java-home

ENV JAVA_VERSION 8u212

RUN set -ex; \
	\
# deal with slim variants not having man page directories (which causes "update-alternatives" to fail)
	if [ ! -d /usr/share/man/man1 ]; then \
		mkdir -p /usr/share/man/man1; \
	fi; \
	\
	apt-get update; \
	apt-get install -y --no-install-recommends \
		openjdk-8-jdk \
	; \
	rm -rf /var/lib/apt/lists/*; \
	\
# verify that "docker-java-home" returns what we expect
	[ "$(readlink -f "$JAVA_HOME")" = "$(docker-java-home)" ]; \
	\
# update-alternatives so that future installs of other OpenJDK versions don't change /usr/bin/java
	update-alternatives --get-selections | awk -v home="$(readlink -f "$JAVA_HOME")" 'index($3, home) == 1 { $2 = "manual"; print | "update-alternatives --set-selections" }'; \
# ... and verify that it actually worked for one of the alternatives we care about
	update-alternatives --query java | grep -q 'Status: manual'

# If you're reading this and have any feedback on how this image could be
# improved, please open an issue or a pull request so we can discuss it!
#
#   https://github.com/docker-library/openjdk/issues

ARG MAVEN_VERSION=3.8.8
ARG USER_HOME_DIR="/root"
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN apt-get update && apt-get install -y --no-install-recommends curl wget

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn


RUN wget -P /tmp https://github.com/ging/fiware-cosmos-orion-spark-connector/releases/download/FIWARE_8.1/orion.spark.connector-1.2.2.1.jar

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
RUN mkdir ./cep

RUN apt install software-properties-common -y \ 
      && add-apt-repository ppa:deadsnakes/ppa -y \
      && apt-get update \
      && apt-get install python3.7 -y \
      && ln -sf /usr/bin/python3.7 /usr/bin/python3 \
      && apt install python3.7-distutils -y

RUN apt install python3-pip -y

COPY runPTP.py ./
COPY ./src ./prediction-job
COPY ./pom.xml ./prediction-job
COPY requirements-maven.txt ./
COPY mvn-entrypoint.sh /usr/local/bin/mvn-entrypoint.sh
COPY settings-docker.xml /usr/share/maven/ref/
RUN pip3 install --no-cache-dir -r requirements-maven.txt
RUN mvn install:install-file -Dfile=/tmp/orion.spark.connector-1.2.2.1.jar -DgroupId=org.fiware.cosmos -DartifactId=orion.spark.connector -Dversion=1.2.2.1 -Dpackaging=jar
RUN cd ./prediction-job

CMD ["python3","-u","./runPTP.py"]