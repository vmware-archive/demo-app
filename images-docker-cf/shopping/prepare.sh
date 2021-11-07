#! /bin/bash

source ../../deploy/src/values.sh
cp ../../$1/target/$1-*.*.*.jar $1.jar
envsubst <  ../../deploy/src/data/$1-app.yaml > $1-app.yaml
envsubst < ../../deploy/src/data/$1-app-tags.yaml > $1-app-tags.yaml 
#envsubst < ../../deploy/src/data/wf-config.yaml > wf-config.yaml
