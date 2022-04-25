#! /bin/zsh

for f in *; do
	if [ -d "$f" ]; then
		echo 
		echo "Building $f..."
		echo 
		./build.sh $f
		echo 
		echo "Finished building $f!"
		echo 
	fi 
done