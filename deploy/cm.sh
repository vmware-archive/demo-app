#! /bin/zsh

### 
## Builds the configmap yaml files from the files in data/
### 

NAMESPACE=tacocat

cp -f ./data/$1/* ./data

./gen-cm.sh data app-config-blue $NAMESPACE > 01_app-config-blue.yaml

sed 's/blue/green/g' 01_app-config-blue.yaml > 01_app-config-green.yaml
