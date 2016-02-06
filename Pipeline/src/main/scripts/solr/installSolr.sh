#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "missing required argument. 1. location of solr install"
    exit 1
fi

SOLR_HOME=$1
SOLR_VERSION=5.4.1
wget http://mirrors.sonic.net/apache/lucene/solr/$SOLR_VERSION/solr-$SOLR_VERSION.tgz
tar xvf solr-$SOLR_VERSION.tgz -C $SOLR_HOME
rm $SOLR_HOME/solr-$SOLR_VERSION.tgz