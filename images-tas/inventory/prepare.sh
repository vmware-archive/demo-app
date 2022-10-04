#! /bin/bash

source ../../deploy/src/values.sh
mkdir inventory
cp -r ../../inventory/ inventory/
envsubst <  ../../deploy/src/data/inventory.conf > inventory.conf
#envsubst < ../../deploy/src/data/wf-config.yaml > wf-config.yaml
