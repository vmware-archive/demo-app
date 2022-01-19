#! /bin/zsh

### 
## Applies the yaml files
### 
source values.sh

echo "   namespace: " $K8S_NAMESPACE
echo " application: " $K8S_APPLICATION
echo "     cluster: " $K8S_CLUSTER
echo "    location: " $K8S_LOCATION
echo "  repository: " $K8S_REPOSITORY

for f in *.yaml ; do
	echo "creating ../$f"
	envsubst < $f > ../$f
done

if [ ! -d "../namespace/" ]
then 
	mkdir ../namespace
fi
for f in namespace/* ; do
	echo "creating ../$f"
	envsubst < $f > ../$f
done

if [ ! -d "../services/" ]
then 
	mkdir ../services
fi
for f in services/* ; do
	if 
	echo "creating ../$f"
	envsubst < $f > ../$f
done

#echo "create ../../payments/src/Payments/applicationTags.yaml"
#envsubst < apptags/applicationTags.yaml > ../../payments/src/Payments/applicationTags.yaml
