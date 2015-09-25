#!/bin/bash

#--------------------
#GCloud setting
export GCLOUD_HOME=/Users/saurabhagarwal/google-cloud-sdk/bin/
export USER_ID=skrollioteamsep2015
export GCLOUD_INSTANCE_NAME=instance-1
export GCLOUD_ZONE=us-central1-f
#---------------------

export PACKAGE_ROOT_DIR=build
export PACKAGE_DIR=skroll-package
export HOME_DIR=$PACKAGE_ROOT_DIR/$PACKAGE_DIR


rm -rf $HOME_DIR
mkdir $HOME_DIR
../gradlew fatjar
../gradlew buildProdApp
cp build/libs/Pipeline-all-1.0.jar $HOME_DIR/
cp src/main/scripts/* $HOME_DIR/
cp -r src/main/webapp/build/ $HOME_DIR/webapp

mkdir $HOME_DIR/config
cp -r src/main/resources/skroll-deploy.properties $HOME_DIR/config/skroll.properties
cp -r src/main/resources/log4j.xml $HOME_DIR/config/log4j.xml

mkdir $HOME_DIR/data
mkdir $HOME_DIR/data/preEvaluated
mkdir $HOME_DIR/data/models
mkdir $HOME_DIR/data/benchmark
mkdir $HOME_DIR/logs
mkdir $HOME_DIR/phantomjs
cp src/main/resources/parser/extractor/* $HOME_DIR/phantomjs/

rm $PACKAGE_ROOT_DIR/skroll-package.tar.gz
tar -czvf $PACKAGE_ROOT_DIR/skroll-package.tar.gz -C $PACKAGE_ROOT_DIR $PACKAGE_DIR
$GCLOUD_HOME/gcloud compute copy-files $PACKAGE_ROOT_DIR/skroll-package.tar.gz $USER_ID@$GCLOUD_INSTANCE_NAME: --zone $GCLOUD_ZONE
