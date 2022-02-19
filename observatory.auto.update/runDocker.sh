#! /bin/bash

mkdir -p results
docker run --rm -it --network host -v "$(pwd)/config:/observatory/config" -v "$(pwd)/results:/observatory/results" observatory $*