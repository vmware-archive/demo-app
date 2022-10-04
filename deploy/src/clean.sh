#! /bin/zsh

for f in *.yaml ; do 
	if test -f "../$f"; then
		echo "deleting ../$f";
		rm ../$f;
	fi
done

for f in namespace/* ; do
	if test -f "../$f"; then 
		echo "deleting ../$f";
		rm ../$f;
	fi
done

if [ -d "../namespace/" ]; then
	echo "deleting ../namespaces/";
	rmdir ../namespace/;
fi

if [ -d "../services/" ]; then
	for f in ../services/* ; do
		echo "services/$f";
		if test -f "../services/$f"; then
			echo "deleting ../services/$f";
			rm ../services/$f;
		fi
	done
	echo "deleting ../services/";
	rmdir  ../services/;
fi

if [ -d "../data/" ]; then
	for f in ../data/* ; do
		echo "deleting ../data/$f";
		rm ../data/$f;
	done
	echo "deleting ../data/";
	rmdir  ../data;
fi

if test -f "01_app-config-blue.yaml"; then
	echo "deleteing 01_app-config-blue.yaml"
	rm  01_app-config-blue.yaml
fi

if test -f "01_app-config-green.yaml"; then
	echo "deleteing 01_app-config-green.yaml"
	rm  01_app-config-green.yaml
fi
