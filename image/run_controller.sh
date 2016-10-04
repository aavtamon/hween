#!/bin/sh

ROOT_DIR=/home/pi/hween/controller
CLOUD_SERVER=192.168.0.116:8080

amixer cset numid=3 2
# change 2 to 1 to reconfigure to headphone jack

sudo java -cp ${ROOT_DIR}/controller.jar:${ROOT_DIR}/lib/json-20080701.jar:${ROOT_DIR}/lib/pi4j-core.jar -Dconfig_file=${ROOT_DIR}/controller.properties -Dserver_url=http://${CLOUD_SERVER}/HweenToy com.piztec.hween.controller.Main
