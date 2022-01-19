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
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
	if 
>>>>>>> 662bcb3 (Improved configuration of the app)
>>>>>>> wavefrontHQ-master
	echo "creating ../$f"
	envsubst < $f > ../$f
done

<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> wavefrontHQ-master
if [ ! -d "../namespace/" ]
then 
	mkdir ../namespace
fi
for f in namespace/* ; do
<<<<<<< HEAD
=======
=======
for f in namespace/* ; do
	if 
>>>>>>> 662bcb3 (Improved configuration of the app)
>>>>>>> wavefrontHQ-master
	echo "creating ../$f"
	envsubst < $f > ../$f
done

<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> wavefrontHQ-master
if [ ! -d "../services/" ]
then 
	mkdir ../services
fi
for f in services/* ; do
	if 
	echo "creating ../$f"
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> wavefrontHQ-master
	envsubst < $f > ../$f
done

#echo "create ../../payments/src/Payments/applicationTags.yaml"
#envsubst < apptags/applicationTags.yaml > ../../payments/src/Payments/applicationTags.yaml
<<<<<<< HEAD
=======
=======
for f in services/* ; do
	if 
	echo "creating ../$f)"
=======
>>>>>>> bac6e66 (Improved build & deploy - secret now created from file)
	envsubst < $f > ../$f
done

declare -a services=("notification"
                     "packaging" 
	             "printing"
                     "payments"
                     "warehouse"
                     "delivery"
                     "styling"
                     "shopping")

for svc in "${services[@]}"
do
 echo export SERVICE=$svc
 export SERVICE=$svc
 if [ "$svc" = "payments" ]
 then
	echo "create ../../$svc/src/Payments/applicationTags.yaml"
    envsubst < apptags/applicationTags.yaml > ../../$svc/src/Payments/applicationTags.yaml
 else 
	echo "creating ../../$svc/applicationTags.yaml"
    envsubst < apptags/applicationTags.yaml > ../../$svc/applicationTags.yaml 
 fi
 
done 
>>>>>>> 662bcb3 (Improved configuration of the app)
>>>>>>> wavefrontHQ-master
