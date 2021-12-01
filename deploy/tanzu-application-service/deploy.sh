#! /bin/sh
IMAGE='NONE'

if [[ -f ../../deploy/src/values.sh ]]; then 
     	source ../../deploy/src/values.sh
else 
        echo "Unable to source \"../../values.sh\" "
	exit
fi

if [ -z ${K8S_REPOSITORY+x} ]; then 
        echo "usage: export K8S_REPOSITORY=[your private registry here]"
        exit
fi

deploy_service() {
        echo "deploying $IMAGE"
        cf push $IMAGE-service --docker-image $K8S_REPOSITORY/$IMAGE:latest --no-manifest --health-check-type process --no-start --no-start --no-route
        cf map-route $IMAGE-service apps.internal --hostname $IMAGE-service 
        #cf start $IMAGE-service
}

for f in ../../images-docker-cf/*; do
	if [ -d "$f" ]; then
                IMAGE=$(basename $f)
                deploy_service
		echo 
	fi 
done


cf start printing-service
cf start packaging-service
cf start notification-service
cf start delivery-service
cf start warehouse-service
cf start payments-service
cf start inventory-service
cf start styling-service
cf start shopping-service
cf start loadgen-service
cf scale -i 5 loadgen-service
