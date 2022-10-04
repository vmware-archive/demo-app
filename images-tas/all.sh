#! /bin/zsh

for f in *; do
	if [ -d "$f" ]; then
		echo 
		echo "Building $f..."
		echo 
		./build.sh $f $1
		echo 
		echo "Finished building $f!"
		echo 
	fi 
done
