#!/bin/bash

source common_defs.sh

echo "****************"
echo "Installation: starting the installation sequence"


echo "Installation: checking if the version file provided"
if [ -f version.properties ]
then
  cp version.properties ${HWEEN_ROOT_DIR}
  echo "Installation: version file updated"
else
  echo "Code Image ERROR: version file is not provided!!! Upgrade will not work properly"
fi

echo "Installation: updating executable scripts if required"
if [ -f common_defs.sh ]
then
  cp common_defs.sh ${HWEEN_ROOT_DIR}
  chmod -R 777 ${HWEEN_ROOT_DIR}/common_defs.sh
fi

if [ -f run_controller.sh ]
then
  cp run_controller.sh ${HWEEN_ROOT_DIR}
  chmod -R 777 ${HWEEN_ROOT_DIR}/run_controller.sh
fi

if [ -f start_code_upgrade.sh ]
then
  cp start_code_upgrade.sh ${HWEEN_ROOT_DIR}
  chmod -R 777 ${HWEEN_ROOT_DIR}/start_code_upgrade.sh
fi

if [ -f code_upgrade.sh ]
then
  cp code_upgrade.sh ${HWEEN_ROOT_DIR}
  chmod -R 777 ${HWEEN_ROOT_DIR}/code_upgrade.sh
fi

echo "Installation: checking if system.properties are provided"
if [ -f system.properties ]
then
  cp system.properties ${SYSTEM_PROPERTIES_FILE}
  echo "Installation: system.properties file is updated"
fi
  
  
  
echo "Installation: updating controller files"

rm -rf ${HWEEN_CONTROLLER_DIR}
mkdir ${HWEEN_CONTROLLER_DIR}

cp controller.jar ${HWEEN_CONTROLLER_DIR}

if [ -d lib ]
then
  if [ ! -d ${HWEEN_CONTROLLER_DIR}/lib ]
  then
    mkdir ${HWEEN_CONTROLLER_DIR}/lib
  fi
  
  cp lib/* ${HWEEN_CONTROLLER_DIR}/lib
fi

chmod -R 777 ${HWEEN_CONTROLLER_DIR}


echo "Installation: updating system services"

if [ -f system/hween.service ]
then
  cp system/hween.service /lib/systemd/system
  systemctl enable hween.service  
fi

if [ -f system/hween-upgrade.service ]
then
  cp system/hween-upgrade.service /lib/systemd/system
  systemctl enable hween-upgrade.service
fi


echo "Installation: installation complete"
echo "****************"


echo "Installation: rebooting"
reboot