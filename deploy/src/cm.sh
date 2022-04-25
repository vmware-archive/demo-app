#! /bin/zsh

### 
## Builds the configmap yaml files from the files in data/
### 

PROXYOR=${1:-proxy}

echo "generating with $PROXYOR"
cp -f ./data/$PROXYOR/* ./data

./gen-cm.sh data app-config-blue  > 01_app-config-blue.yaml

sed 's/blue/green/g' 01_app-config-blue.yaml > 01_app-config-green.yaml

./create-yaml.sh
