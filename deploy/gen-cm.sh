#! /bin/zsh

FILE=${1:-.}
NAME=${2:-config}
NAMESPACE=${3:-default}

echo "apiVersion: v1" 
echo "kind: ConfigMap"
echo "metadata:" 
echo "  name: $NAME" 
echo "  namespace: $NAMESPACE" 
echo "data:"

for f in $FILE/*.* ; do
	echo "  $(basename $f): |"
	sed 's_^_    _' $f
done
