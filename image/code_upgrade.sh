#!/bin/bash

source $(dirname "$0")/common_defs.sh

echo "Detecting local code version"
local_version=`grep version ${HWEEN_ROOT_DIR}/version.properties | awk '{ print $2 }'`
echo "The local version is ${local_version}"

while :
do
  echo "Checking code version on the server"

  serialNumber=`grep serial_number ${SYSTEM_PROPERTIES_FILE} | awk '{ print $2 }'`
  secret=`grep secret ${SYSTEM_PROPERTIES_FILE} | awk '{ print $2 }'`

  echo "   Device's serial number: ${serialNumber}"
  echo "   Device's secret: ${secret}"

  code_version=`curl -X GET http://${HWEEN_CLOUD_SERVER}/HweenToy/device/${serialNumber}/code_version -H "secret: ${secret}"`

  if [ "${code_version}" == "" ]
  then
    echo "Server does not report a code version. The update is corrupt or a server is not accessible. Skipping."
  else
    echo "Code version available on the server: ${code_version}"
    

    echo "Comparing with the local version"

    if [ "${code_version}" == "${local_version}" ]
    then
      echo "Versions are identical - nothing to do. Will retry later."
    else
      echo "Server provides an update - starting the download"

      cd ${HWEEN_ROOT_DIR}
      rm -rf tmp_upgrade
      mkdir tmp_upgrade

      cd tmp_upgrade

      curl -X GET http://${HWEEN_CLOUD_SERVER}/HweenToy/device/${serialNumber}/code_image -H "secret: ${secret}" > image.jar
      if [ -f image.jar ]
      then
        tar -xvf image.jar

        if [ -f install.sh ]
        then
          chmod 777 install.sh

          echo "Starting installation of the new image..."
          sudo ./install.sh
        else
          echo "Code download failed - the image file is corrupted. Will retry later."
        fi
      else
        echo "Code download failed - no file received from the server. Will retry later."
      fi

      echo "Cleaning up temporary installation folder..."
      cd ${HWEEN_ROOT_DIR}
      rm -rf tmp_upgrade
    fi
  fi

  sleep 60
done


