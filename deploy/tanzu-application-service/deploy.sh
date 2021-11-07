#! /bin/sh

if [[ -f ../../deploy/src/values.sh ]]; then 
     	source ../../deploy/src/values.sh
else 
        echo "Unable to source \"../../values.sh\" "
	exit
fi

if [ -z ${K8S_REPOSITORY+x} ]; then 
        echo "nusage: export K8S_REPOSITORY=[your private registry here]"
        exit
fi

cf stop delivery-service
cf stop inventory-service
cf stop loadgen-service 
cf stop notification-service 
cf stop packaging-service 
cf stop payments-service 
cf stop printing-service 
cf stop styling-service 
cf stop warehouse-service 
cf stop shopping-service 
	
cf push delivery-service --docker-image $K8S_REPOSITORY/delivery:latest --no-manifest --health-check-type process --no-start
cf push inventory-service --docker-image $K8S_REPOSITORY/inventory:latest --no-manifest --health-check-type process --no-start
cf push loadgen-service --docker-image $K8S_REPOSITORY/loadgen:latest --no-manifest --health-check-type process --no-start
cf push notification-service --docker-image $K8S_REPOSITORY/notification:latest --no-manifest --health-check-type process --no-start
cf push packaging-service --docker-image $K8S_REPOSITORY/packaging:latest --no-manifest --health-check-type process --no-start
cf push payments-service --docker-image $K8S_REPOSITORY/payments:latest --no-manifest --health-check-type process --no-start
cf push printing-service --docker-image $K8S_REPOSITORY/printing:latest --no-manifest --health-check-type process --no-start
cf push styling-service --docker-image $K8S_REPOSITORY/styling:latest --no-manifest --health-check-type process --no-start
cf push warehouse-service --docker-image $K8S_REPOSITORY/warehouse:latest --no-manifest --health-check-type process --no-start
cf push shopping-service --docker-image $K8S_REPOSITORY/shopping:latest --no-manifest --health-check-type process --no-start
	
