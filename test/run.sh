#!/bin/bash
cp ../shopping/target/shopping-2.6.2.jar .
java -jar shopping-2.6.2.jar server shopping-app.yaml 
