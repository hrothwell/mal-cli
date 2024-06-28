#!/bin/bash
./gradlew clean build
docker build . -t hrothwell/mal-cli:latest
