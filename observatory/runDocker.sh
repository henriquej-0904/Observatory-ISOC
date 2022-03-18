#! /bin/bash

mkdir -p results
docker run --rm -it -v "$(pwd)/config:/observatory/config" -v "$(pwd)/results:/observatory/results" observatory $*