#!/bin/bash
if [ "$ANDROID_SDK_ROOT" = "" ]; then
  echo set ANDROID_SDK_ROOT !
  exit
fi

#$ANDROID_NDK/ndk-build
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_NAME=`basename $DIR`
echo PROJECT_NAME=$PROJECT_NAME

$ANDROID_SDK_ROOT/tools/android \
 update project --name $PROJECT_NAME --path . --target "android-19"

ant debug

#TODO: rename apk w/project version
