#!/bin/sh

. common_defs.sh

if [ -f version.properties ]
then
  cp version.properties ${HWEEN_CONTROLLER_DIR}
else
  echo "Code Image ERROR: version file is not provided!!! Upgrade will not work properly"
fi


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

if [ -f run_controller.sh ]
then
  cp run_controller.sh ${HWEEN_ROOT_DIR}
  chmod -R 777 ${HWEEN_ROOT_DIR}/code_upgrade.sh
fi

if [ -f system.properties ]
then
  cp system.properties ${SYSTEM_PROPERTIES_FILE}
fi
  
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

#reboot