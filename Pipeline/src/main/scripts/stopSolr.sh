#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "missing required argument. 1. location of solr install
    exit 1
fi

SOLR_HOME=$1
$SOLR_HOME/bin/solr stop