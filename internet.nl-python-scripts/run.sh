#! /bin/bash

mkdir -p results
docker run --rm -it --network host -v "$(pwd)/domains:/internet.nl_batch_scripts/domains" \
-v "$(pwd)/results:/internet.nl_batch_scripts/results" \
-v "$(pwd)/batch.conf:/internet.nl_batch_scripts/batch.conf" \
-v "/etc/passwd:/etc/passwd:ro" -u $UID internet-nl-python-scripts $*
