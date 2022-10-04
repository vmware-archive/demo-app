#! /bin/zsh

if [[ -f ../deploy/src/values.sh ]]; then 
     	source ../deploy/src/values.sh
else 
        echo "\n\nUNABLE TO SOURCE values.sh\n\n"
	exit
fi

# if [ -z ${K8S_REPOSITORY+x} ]; then 
#         echo "\n\nusage: export K8S_REPOSITORY=[your private registry here]\n\n"
#         exit
# fi

if [ -z ${1+x} ]; then 
        echo "\n\nusage: ./build.sh [directory]\n\n"
        exit
fi

cd $1

#remove trailing slash from directory 
IMAGE=$(echo $1 | sed 's:/*$::')
echo "\n>>> Creating image \"$IMAGE\" \n"

if [[ -f prepare.sh ]]; then
	./prepare.sh 
else 
	echo "\n >>> no prepare.sh not found\n"
fi

docker build . -t ${K8S_REPOSITORY}$IMAGE 

if [${K8S_REPOSITORY} ]; then 
        echo -e "\npushing to ${K8S_REPOSITORY}$IMAGE \n"
	docker push ${K8S_REPOSITORY}$IMAGE
else
        echo -e "\nnot pushing\n"
fi

if [[ -f clean.sh ]]; then
	./clean.sh 
fi