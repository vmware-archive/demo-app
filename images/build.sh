#! /bin/zsh

source ../deploy/src/values.sh

cd $1

if [[ -f prepare.sh ]]; then
	./prepare.sh
fi

docker build . -t $K8S_REPOSITORY/$1
docker push $K8S_REPOSITORY/$1

if [[ -f clean.sh ]]; then
	./clean.sh
fi


