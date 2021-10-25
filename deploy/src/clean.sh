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

for f in services/* ; do
	if test -f "../$f"; then
		echo "deleting ../$f";
		rm ../$f;
	fi
done
