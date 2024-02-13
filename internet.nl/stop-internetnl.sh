#! /bin/bash

# Stop Internet.nl service
env -i RELEASE=main docker compose --env-file=docker/defaults.env --env-file=docker/host.env --env-file=docker/local.env down
