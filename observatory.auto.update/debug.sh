#! /bin/bash

mvn clean compile assembly:single
docker build -t observatory .
docker run --rm -it --network host -v "$(pwd)/config:/app/config" observatory /bin/bash