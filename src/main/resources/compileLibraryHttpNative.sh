#!/usr/bin/env bash

# create directory structure if not exist
if [[ `uname` == 'Linux' ]]; then
   export libname='libHttpNative.so'
   export platform='linux'
   export JAVA_HOME='<fill-in-the-jdk-location>'
elif [[ `uname` == 'Darwin' ]]; then
   export libname='libHttpNative.dylib'
   export platform='darwin'
   export JAVA_HOME='/Library/Java/JavaVirtualMachines/jdk1.8.0_31.jdk/Contents/Home'
fi

[ -d build/ ] || mkdir build/
[ -d build/binaries/ ] || mkdir build/binaries/
[ -d build/binaries/debug/ ] || mkdir build/binaries/debug/
[ -d "build/binaries/debug/$platform/" ] || mkdir "build/binaries/debug/$platform/"

gcc -fpic -shared -o "build/binaries/debug/$platform/$libname" -lcurl \
    src/main/jni/HttpNative.c \
    -I "$JAVA_HOME/include/" \
    -I "$JAVA_HOME/include/$platform/"

