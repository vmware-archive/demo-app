#! /bin/zsh

cd $1

if [[ -f prepare.sh ]]; then
	./prepare.sh
fi

docker build . -t wfharbor.eng.vmware.com/demo-app/$1
docker push wfharbor.eng.vmware.com/demo-app/$1

if [[ -f clean.sh ]]; then
	./clean.sh
fi

cd ..
