#!/bin/bash

if [ "$#" -ne 2 ]; then
    echo "missing required arguments. 2 arguments are required - 1. location of solr install and 2. location of autocomplete data files to create indexes"
    exit 1
fi

SOLR_HOME=$1
AUTOCOMPLETE_DIR=$2
$SOLR_HOME/bin/solr delete -c autocomplete
$SOLR_HOME/bin/solr create_core -c autocomplete -d $AUTOCOMPLETE_DIR/conf/
$SOLR_HOME/bin/post -c autocomplete $AUTOCOMPLETE_DIR/cik_ticker.csv -params "&separator=|&fieldnames=id,field1,field2,,,&literal.type=company"
$SOLR_HOME/bin/post -c autocomplete $AUTOCOMPLETE_DIR/categories.csv -params "&separator=,&fieldnames=id,field1,field2&literal.type=category"
$SOLR_HOME/bin/post -c autocomplete $AUTOCOMPLETE_DIR/formtype.csv -params "&separator=,&fieldnames=id,field1&literal.type=formtype"