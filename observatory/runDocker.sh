#! /bin/bash

mkdir -p results
docker run --rm -it -v "$(pwd)/config:/observatory/config" -v "$(pwd)/results:/observatory/results" henriquej0904/observatory-isoc:1.3.1 $*