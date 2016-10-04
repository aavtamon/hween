#!/bin/sh

ROOT_DIR=/home/pi/hween/controller

rm -rf ${ROOT_DIR}
mkdir ${ROOT_DIR}

cp controller.properties ${ROOT_DIR}
cp controller.tar ${ROOT_DIR}
cp run_controller.sh ${ROOT_DIR}

mkdir ${ROOT_DIR}/lib
cp lib/* ${ROOT_DIR}/lib

sudo cp system/hween.service /lib/systemd/system
sudo systemctl enable hween.service

//sudo reboot