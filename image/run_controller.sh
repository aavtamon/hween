#!/bin/sh

. common_defs.sh

amixer cset numid=3 2
# change 2 to 1 to reconfigure to headphone jack

sudo java -cp ${HWEEN_CONTROLLER_DIR}/controller.jar:${HWEEN_CONTROLLER_DIR}/lib/json-20080701.jar:${HWEEN_CONTROLLER_DIR}/lib/pi4j-core.jar -Dconfig_file=${SYSTEM_PROPERTIES_FILE} -Dserver_url=http://${HWEEN_CLOUD_SERVER}/HweenToy com.piztec.hween.controller.Main
