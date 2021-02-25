#!/bin/bash

cd "$(dirname $(realpath $0))" # Make sure relative path are from this script file

docker build -t poker .

docker save poker > poker.docker

docker image prune -f
