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
	rm -rf  ../namespace/;
fi

for f in services/* ; do
	if test -f "../$f"; then
		echo "deleting ../$f";
		rm ../$f;
	fi
done

if [ -d "../services/" ]; then
	echo "deleting ../services/";
	rm -rf ../services/;
fi

rm  01_app-config-blue.yaml
rm  01_app-config-green.yaml
