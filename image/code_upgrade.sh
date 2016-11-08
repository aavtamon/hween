#!/bin/sh

UPGRADE_SERVER=192.168.0.10:8080

#RUN_DIR=/home/pi/hween/
RUN_DIR=/Users/aavtamonov/project/other/hween/image

PROPERTIES_FILE=${RUN_DIR}/controller.properties


echo "Checking code version on the server for "

serialNumber=`grep serial_number ${PROPERTIES_FILE} | awk '{ print $2 }'`
secret=`grep secret ${PROPERTIES_FILE} | awk '{ print $2 }'`

echo "   Device's serial number: ${serialNumber}"
echo "   Device's secret: ${secret}"

code_version=`curl -X GET http://${UPGRADE_SERVER}/HweenToy/device/${serialNumber}/code_version -H "secret: ${secret}"`

echo "Code version available on the server: ${code_version}"
