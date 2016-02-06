#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "missing required argument. 1. location of solr install"
    exit 1
fi

SOLR_HOME=$1
cp solr/web.xml $SOLR_HOME/server/solr-webapp/webapp/WEB-INF/
$SOLR_HOME/bin/solr start