#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "missing required arguments. 2 arguments are required - 1. location of data directory and 2. location of log directory"
    exit 1
fi

DATA_DIR=$1
LOG_DIR=$2
mkdir $DATA_DIR/data
mkdir $DATA_DIR/data/preEvaluated
mkdir $DATA_DIR/data/models
mkdir $DATA_DIR/data/benchmark
mkdir $LOG_DIR/logs
ln -s $DATA_DIR/data data
ln -s $LOG_DIR/logs logs