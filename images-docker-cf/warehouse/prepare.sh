#! /bin/bash

cp -r ../../warehouse .
envsubst <  ../../deploy/src/data/$1-app.yaml > $1-app.yaml
envsubst < ../../deploy/src/data/$1-app-tags.yaml > $1-app-tags.yaml 
