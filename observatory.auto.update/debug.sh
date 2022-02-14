#! /bin/bash

mvn clean compile assembly:single
docker build -t observatory .
mkdir -p results
docker run --rm -it --network host -v "$(pwd)/config:/app/config" -v "$(pwd)/results:/app/results" observatory /bin/bash