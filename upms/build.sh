#!/usr/bin/env bash
DOCKER_IMAGE='upms'
cd `dirname "$0"`
mvn -Pfat-jar -Dmaven.test.skip=true clean package
docker build --build-arg HTTP_PROXY=http://vicliu.i234.me:8001 -t vicliu.i234.me:8001/yn-etc/$DOCKER_IMAGE:latest .
docker push vicliu.i234.me:8001/yn-etc/$DOCKER_IMAGE:latest
