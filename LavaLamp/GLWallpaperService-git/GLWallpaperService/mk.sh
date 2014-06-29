#!/bin/bash
if [ "$ANDROID_SDK_ROOT" = "" ]; then
  echo set ANDROID_SDK_ROOT !
  exit
fi

#TODO:?set app_name in res/values/strings.xml?
#$ANDROID_NDK/ndk-build
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_NAME=`basename $DIR`
echo PROJECT_NAME=$PROJECT_NAME

$ANDROID_SDK_ROOT/tools/android \
 update project --name $PROJECT_NAME --path . --target "android-11" $1 $2 $3 $4 $5 $6 $7 $8 $9

ant debug

#TODO: rename apk w/project version
