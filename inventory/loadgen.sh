#!/bin/bash

echo "Send requests every $1 seconds"

while [ true ]
do
	quantity=$((1 + RANDOM % 20))
	curl_cmd="curl http://localhost:60001/inventory/available/35" 
	echo $curl_cmd
	eval $curl_cmd
	curl_cmd="curl -X POST http://localhost:60001/inventory/checkout/45"
	echo $curl_cmd
	eval $curl_cmd
	sleep $1
	printf "\n"
done
