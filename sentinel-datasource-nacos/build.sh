#!/usr/bin/env bash
DOCKER_IMAGE='sentinel-datasource-nacos'
cd `dirname "$0"`
mvn -Pfat-jar -Dmaven.test.skip=true clean package
docker build -t microservice105:8443/yn-etc/$DOCKER_IMAGE:latest .
docker push microservice105:8443/yn-etc/$DOCKER_IMAGE:latest
