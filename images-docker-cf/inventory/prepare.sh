#! /bin/bash

cp ../../inventory/target/amd64/inventory inventory
envsubst <  ../../deploy/src/data/inventory.conf > inventory.conf
