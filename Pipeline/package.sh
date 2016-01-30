#!/bin/bash

#--------------------
#GCloud setting
export USER_ID=skrollioteam2016
export GCLOUD_INSTANCE_NAME=instance-1
export GCLOUD_ZONE=us-east1-b
#---------------------

export PACKAGE_ROOT_DIR=build
export PACKAGE_DIR=skroll-package
export HOME_DIR=$PACKAGE_ROOT_DIR/$PACKAGE_DIR

../gradlew fatjar
../gradlew buildProdApp

rm -rf $HOME_DIR
mkdir $HOME_DIR
cp build/libs/Pipeline-all-1.0.jar $HOME_DIR/
cp src/main/scripts/* $HOME_DIR/
cp -r src/main/webapp/build/ $HOME_DIR/webapp

mkdir $HOME_DIR/config
cp -r src/main/resources/skroll-deploy.properties $HOME_DIR/config/skroll.properties
cp -r src/main/resources/log4j.xml $HOME_DIR/config/log4j.xml

mkdir $HOME_DIR/si
cp -R src/main/si/* $HOME_DIR/si/

mkdir $HOME_DIR/phantomjs
cp src/main/resources/parser/extractor/* $HOME_DIR/phantomjs/

mkdir $HOME_DIR/autocomplete
cp -r src/main/resources/solrconf/ $HOME_DIR/autocomplete/solrconf/
cp src/main/resources/cik_ticker.csv $HOME_DIR/autocomplete/
cp src/main/resources/categories.csv $HOME_DIR/autocomplete/
cp src/main/resources/formtype.csv $HOME_DIR/autocomplete/

rm $PACKAGE_ROOT_DIR/skroll-package.tar.gz
tar -czvf $PACKAGE_ROOT_DIR/skroll-package.tar.gz -C $PACKAGE_ROOT_DIR $PACKAGE_DIR
gcloud compute copy-files $PACKAGE_ROOT_DIR/skroll-package.tar.gz $USER_ID@$GCLOUD_INSTANCE_NAME: --zone $GCLOUD_ZONE
