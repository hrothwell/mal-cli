#!/bin/bash

docker run --mount type=bind,source="$PATH_TO_MAL_CLI",target="/root/mal-cli" -p "8080:8080" "hrothwell/mal-cli:latest" $@
