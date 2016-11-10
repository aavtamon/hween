#!/bin/sh

if [$1 == ""]
then
  echo "You must provide a version number"
  exit
fi

mkdir tmp
cp image/controller.jar tmp
cp image/system.properties tmp
cp image/install.sh tmp
cp image/start_code_upgrade.sh tmp
cp image/code_upgrade.sh tmp
cp image/start_controller.sh tmp
cp image/controller.sh tmp
cp image/common_defs.sh tmp
cp -r image/lib tmp
cp -r image/system tmp
echo "version: $1" > tmp/version.properties

rm image.tar
cd tmp
tar -cvf ../image.tar *
cd ..
rm -rf tmp

echo
echo "****************************"
echo "image.tar for version $1 is created/updated"
echo "****************************"
