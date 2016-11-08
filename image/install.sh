#!/bin/sh

RUN_DIR=/home/pi/hween/

cp run_controller.sh ${RUN_DIR}
chmod -R 777 ${RUN_DIR}/run_controller.sh

cp code_upgrade.sh ${RUN_DIR}
chmod -R 777 ${RUN_DIR}/code_upgrade.sh


CONTROLLER_DIR=${RUN_DIR}/controller

rm -rf ${CONTROLLER_DIR}
mkdir ${CONTROLLER_DIR}

cp controller.properties ${CONTROLLER_DIR}
cp controller.jar ${CONTROLLER_DIR}
cp version.json ${CONTROLLER_DIR}

mkdir ${CONTROLLER_DIR}/lib
cp lib/* ${CONTROLLER_DIR}/lib

chmod -R 777 ${CONTROLLER_DIR}

cp system/hween.service /lib/systemd/system
systemctl enable hween.service

cp system/hween-upgrade.service /lib/systemd/system
systemctl enable hween-upgrade.service

#reboot