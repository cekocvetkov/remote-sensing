#!/bin/bash

# Build quarkus backend jar
cd remote-sensing-api-backend
./gradlew build -Dquarkus.package.type=legacy-jar -Dquarkus.package.output-directory=../../quarkus-docker

cd ..
docker-compose up -d
