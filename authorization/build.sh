#!/usr/bin/env bash
cd `dirname "$0"`
mvn -Dmaven.test.skip=true clean package
docker build -t registry.cn-chengdu.aliyuncs.com/magicletters/authorization:latest .
docker push registry.cn-chengdu.aliyuncs.com/magicletters/authorization:latest