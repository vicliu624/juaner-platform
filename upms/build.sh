#!/usr/bin/env bash
DOCKER_IMAGE='upms'
cd `dirname "$0"`
mvn -Pfat-jar -Dmaven.test.skip=true clean package
docker build -t registry.cn-chengdu.aliyuncs.com/magicletters/$DOCKER_IMAGE:latest .
docker push registry.cn-chengdu.aliyuncs.com/magicletters/$DOCKER_IMAGE:latest
