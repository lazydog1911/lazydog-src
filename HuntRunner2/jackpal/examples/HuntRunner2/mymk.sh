#if [ "$ANDROID_NDK" = "" ]; then
#  echo set ANDROID_NDK !
#  exit
#fi

#$ANDROID_NDK/ndk-build
#DIR_NAME=`dirname $0`
#PROJECT_NAME=`basename $DIR_NAME`
#echo "$PROJECT_NAME" ;exit

$ANDROID_SDK_ROOT/tools/android  update project --name HuntRunner2 --path . --target "android-11" --library ../../libraries/emulatorview
ant release
#TODO: rename apk w/project version
