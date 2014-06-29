find -name '*.o' -delete
find -name '*.so' -delete
find -name '*.apk' -delete
find -name '*.class' -delete
#TODO: kill .a files?
rm -v build.xml
rm -v  bin/classes.dex bin/classes.dex.d

