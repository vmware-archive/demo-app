#! /bin/zsh

if [[ -f ../deploy/src/values.sh ]]; then 
     	source ../deploy/src/values.sh
else 
        echo "\n\nUNABLE TO SOURCE values.sh\n\n"
	exit
fi

if [ -z ${K8S_REPOSITORY+x} ]; then 
        echo "\n\nusage: export K8S_REPOSITORY=[your private registry here]\n\n"
        exit
fi

if [ -z ${1+x} ]; then 
        echo "\n\nusage: ./build.sh [directory]\n\n"
        exit
fi

if test -d $1; then
        cd $1;
else    
	echo "\n >>> directory $1 does not exist";
        exit;
fi


# remove trailing slash
IMAGE=$(echo $1 | sed 's:/*$::')
echo "\n>>> Creating image \"$IMAGE\" and pushing to \"${K8S_REPOSITORY}$IMAGE\"\n"

if [[ -f prepare.sh ]]; then
	./prepare.sh $IMAGE
else 
	echo ">>> no prepare.sh not found\n"
	exit;
fi

if [ $IMAGE = 'loadgen' ]; then
        echo "building loadgen"
        echo $TASDOMAIN
        if [ -z ${TASDOMAIN+x} ]; then 
                echo "TASDOMAIN not found"
                exit
        fi
        docker build . -t ${K8S_REPOSITORY}$IMAGE  --build-arg SERVICE_NAME=$IMAGE --build-arg TAS_DOMAIN=$TASDOMAIN
else
        docker build . -t ${K8S_REPOSITORY}$IMAGE  --build-arg SERVICE_NAME=$IMAGE 
fi

if [ -z ${2+x} ]; then 
	docker push ${K8S_REPOSITORY}$IMAGE
else
        echo "\n\nnot pushing"
fi

if [[ -f clean.sh ]]; then
	./clean.sh $IMAGE
fi


