#! /bin/zsh

cd $1

if [ -z "${REPOSITORY_PREFIX}" ]
then 
    echo "Please set the REPOSITORY_PREFIX"
else 
	if [[ -f prepare.sh ]]; then
		./prepare.sh
	fi

	docker build . -t $REPOSITORY_PREFIX/$1
	docker push $REPOSITORY_PREFIX/$1

	if [[ -f clean.sh ]]; then
		./clean.sh
	fi
fi

cd ..
